package me.huntto.brush

import me.huntto.brush.command.AddInkCommand
import me.huntto.brush.command.CleanCommand
import me.huntto.brush.command.CommandCallback
import me.huntto.brush.command.ICommand
import me.huntto.brush.content.Ink
import me.huntto.brush.view.IBoardView


class Board(boardWidth: Int, boardHeight: Int, boardView: IBoardView) {

    val brush: Brush = Brush(boardWidth, boardHeight, boardView)
    var inks: ArrayList<Ink> = ArrayList<Ink>()
        set(value) {
            field = value
            brush.draw(value)
        }
    var onCommandSizeChangedListener: ((redoCommandSize: Int, undoCommandSize: Int) -> Unit)? = null

    private val redoCommands = ArrayList<ICommand>()
    private val undoCommands = ArrayList<ICommand>()

    private val commandCallback = object : CommandCallback {
        override fun addInks(inks: ArrayList<Ink>) {
            this@Board.inks.addAll(inks)
        }

        override fun removeInks(inks: ArrayList<Ink>) {
            this@Board.inks.removeAll(inks)
        }

        override fun addInk(ink: Ink) {
            inks.add(ink)
        }

        override fun removeInk(ink: Ink) {
            inks.remove(ink)
        }

    }

    private fun notifyCommandSizeChanged() {
        onCommandSizeChangedListener?.invoke(redoCommands.size, undoCommands.size)
    }

    init {
        brush.onGenerateInkListener = { newInk ->
            redoCommands.clear()
            undoCommands.add(AddInkCommand(newInk, commandCallback))
            inks.add(newInk)
            notifyCommandSizeChanged()
        }
    }

    fun clean() {
        redoCommands.clear()
        undoCommands.add(CleanCommand(ArrayList<Ink>(inks), commandCallback))

        inks.clear()
        brush.clean()

        notifyCommandSizeChanged()
    }

    fun undo() {
        undoCommands.lastOrNull()?.let {
            undoCommands.remove(it)
            it.undo()
            redoCommands.add(it)

            brush.draw(inks)
        }

        notifyCommandSizeChanged()
    }

    fun redo() {
        redoCommands.lastOrNull()?.let {
            redoCommands.remove(it)
            it.redo()
            undoCommands.add(it)

            brush.draw(inks)
        }

        notifyCommandSizeChanged()
    }
}