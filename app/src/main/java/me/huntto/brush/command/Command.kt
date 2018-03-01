package me.huntto.brush.command

import me.huntto.brush.content.Ink
import me.huntto.brush.log.logD

interface ICommand {
    fun undo()
    fun redo()
}

interface CommandCallback {
    fun addInks(inks: ArrayList<Ink>)
    fun removeInks(inks: ArrayList<Ink>)

    fun addInk(ink: Ink)
    fun removeInk(ink: Ink)
}

class CleanCommand(private val inks: ArrayList<Ink>, private val callback: CommandCallback) : ICommand {

    override fun undo() {
        logD("undo")
        callback.addInks(inks)
    }

    override fun redo() {
        logD("redo")
        callback.removeInks(inks)
    }

}

class AddInkCommand(private val ink: Ink, private val callback: CommandCallback) : ICommand {

    override fun undo() {
        logD("undo")
        callback.removeInk(ink)
    }

    override fun redo() {
        logD("redo")
        callback.addInk(ink)
    }
}