package com.mukusuzuki.posmuku.plugins

import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPCellEvent
import com.itextpdf.text.pdf.PdfPTable

internal abstract class CustomBorder(border: Int) : PdfPCellEvent {
    private var border = 0

    init {
        this.border = border
    }

    override fun cellLayout(
        cell: PdfPCell, position: Rectangle,
        canvases: Array<PdfContentByte>
    ) {
        val canvas = canvases[PdfPTable.LINECANVAS]
        canvas.saveState()
        setLineDash(canvas)
        if (border and PdfPCell.TOP == PdfPCell.TOP) {
            canvas.moveTo(position.right, position.top)
            canvas.lineTo(position.left, position.top)
        }
        if (border and PdfPCell.BOTTOM == PdfPCell.BOTTOM) {
            canvas.moveTo(position.right, position.bottom)
            canvas.lineTo(position.left, position.bottom)
        }
        if (border and PdfPCell.RIGHT == PdfPCell.RIGHT) {
            canvas.moveTo(position.right, position.top)
            canvas.lineTo(position.right, position.bottom)
        }
        if (border and PdfPCell.LEFT == PdfPCell.LEFT) {
            canvas.moveTo(position.left, position.top)
            canvas.lineTo(position.left, position.bottom)
        }
        canvas.stroke()
        canvas.restoreState()
    }

    abstract fun setLineDash(canvas: PdfContentByte)
}

internal class SolidBorder(border: Int) : CustomBorder(border) {
    override fun setLineDash(canvas: PdfContentByte) {}
}