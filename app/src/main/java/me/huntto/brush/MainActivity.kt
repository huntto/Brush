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
