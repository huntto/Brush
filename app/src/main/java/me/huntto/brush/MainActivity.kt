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

package me.huntto.brush

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.huntto.brush.content.Ink
import me.huntto.brush.log.logD

class MainActivity : AppCompatActivity() {
    private lateinit var board: Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD("onCreate")
        setContentView(R.layout.activity_main)
        boardView.onAvailableListener = { width, height ->
            board = Board(width, height, boardView)
            boardView.setOnTouchListener(board.brush)
            board.brush.maxPointer = 2

            board.onCommandSizeChangedListener = { redoCommandSize: Int, undoCommandSize: Int ->
                redoBtn.isEnabled = redoCommandSize != 0
                undoBtn.isEnabled = undoCommandSize != 0
            }
        }

        brushBtn.setOnClickListener {
            board.brush.type = Ink.Type.PENCIL
            eraserBtn.isChecked = false
            brushBtn.isChecked = true
        }
        eraserBtn.setOnClickListener {
            board.brush.type = Ink.Type.ERASER
            eraserBtn.isChecked = true
            brushBtn.isChecked = false
        }

        redoBtn.setOnClickListener { board.redo() }
        undoBtn.setOnClickListener { board.undo() }
        cleanBtn.setOnClickListener { board.clean() }
    }
}
