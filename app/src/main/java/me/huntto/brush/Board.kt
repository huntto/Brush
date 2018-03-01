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
    var onCommandSizeChangedListener: OnCommandSizeChangedListener? = null

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

    private val onGenerateInkListener = object : Brush.OnGenerateInkListener {
        override fun onGenerate(ink: Ink) {
            redoCommands.clear()
            undoCommands.add(AddInkCommand(ink, commandCallback))
            inks.add(ink)
            onCommandSizeChangedListener?.onCommandSizeChanged(redoCommands.size, undoCommands.size)
        }
    }

    init {
        brush.onGenerateInkListener = onGenerateInkListener
    }

    fun clean() {
        redoCommands.clear()
        undoCommands.add(CleanCommand(ArrayList<Ink>(inks), commandCallback))

        inks.clear()
        brush.clean()

        onCommandSizeChangedListener?.onCommandSizeChanged(redoCommands.size, undoCommands.size)
    }

    fun undo() {
        undoCommands.lastOrNull()?.let {
            undoCommands.remove(it)
            it.undo()
            redoCommands.add(it)

            brush.draw(inks)
        }

        onCommandSizeChangedListener?.onCommandSizeChanged(redoCommands.size, undoCommands.size)
    }

    fun redo() {
        redoCommands.lastOrNull()?.let {
            redoCommands.remove(it)
            it.redo()
            undoCommands.add(it)

            brush.draw(inks)
        }

        onCommandSizeChangedListener?.onCommandSizeChanged(redoCommands.size, undoCommands.size)
    }

    interface OnCommandSizeChangedListener {
        fun onCommandSizeChanged(redoCommandSize: Int, undoCommandSize: Int)
    }
}