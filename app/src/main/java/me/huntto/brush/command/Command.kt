/*
 * MIT License
 *
 * Copyright (c) 2018 xiangtao
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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