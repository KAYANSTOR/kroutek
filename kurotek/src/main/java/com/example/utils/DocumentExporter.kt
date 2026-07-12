package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DocumentExporter {

    /**
     * Prints an HTML-styled report natively using Android's PrintManager.
     * This supports WiFi, Bluetooth, and Thermal printers with built-in system print preview!
     */
    fun printReport(context: Context, title: String, headers: List<String>, rows: List<List<String>>) {
        try {
            val htmlContent = generateHtmlTable(title, headers, rows)
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val webView = android.webkit.WebView(context)
            webView.webViewClient = object : android.webkit.WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                    val printAdapter = webView.createPrintDocumentAdapter(title)
                    printManager.print(title, printAdapter, PrintAttributes.Builder().build())
                }
            }
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            Log.e("PRINT_REPORT", "Error in printReport", e)
            Toast.makeText(context, "فشل بدء عملية الطباعة: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Exports data to a beautiful HTML-formatted Excel file (XLS).
     * Universal compatibility with MS Excel, Google Sheets, and LibreOffice. Natively supports RTL.
     */
    fun exportToExcel(context: Context, fileName: String, title: String, headers: List<String>, rows: List<List<String>>) {
        try {
            val htmlBuilder = StringBuilder()
            htmlBuilder.append("\uFEFF") // UTF-8 BOM
            htmlBuilder.append("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">")
            htmlBuilder.append("<head><meta http-equiv=\"content-type\" content=\"application/vnd.ms-excel; charset=UTF-8\"></head>")
            htmlBuilder.append("<body dir=\"rtl\">")
            htmlBuilder.append("<table border=\"1\" style=\"border-collapse:collapse; font-family:sans-serif;\">")
            
            // Title Row
            htmlBuilder.append("<tr><th colspan=\"${headers.size}\" style=\"background-color:#DC2626; color:white; font-size:16px; font-weight:bold; height:45px;\">$title</th></tr>")
            
            // Date Row
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            htmlBuilder.append("<tr><th colspan=\"${headers.size}\" style=\"background-color:#F1F5F9; color:#475569; font-size:11px; height:25px;\">تاريخ التصدير: $dateStr</th></tr>")

            // Header Row
            htmlBuilder.append("<tr style=\"background-color:#1E293B; color:white; font-weight:bold; height:35px;\">")
            for (header in headers) {
                htmlBuilder.append("<th style=\"padding:10px; border:1px solid #94A3B8;\">$header</th>")
            }
            htmlBuilder.append("</tr>")
            
            // Data Rows
            var index = 0
            for (row in rows) {
                val bg = if (index % 2 == 1) "#F8FAFC" else "#FFFFFF"
                htmlBuilder.append("<tr style=\"background-color:$bg; height:30px;\">")
                for (cell in row) {
                    htmlBuilder.append("<td style=\"padding:8px; border:1px solid #E2E8F0; text-align:center;\">$cell</td>")
                }
                htmlBuilder.append("</tr>")
                index++
            }
            
            htmlBuilder.append("</table>")
            htmlBuilder.append("</body>")
            htmlBuilder.append("</html>")
            
            val file = File(context.cacheDir, "${fileName}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.xls")
            file.writeText(htmlBuilder.toString(), StandardCharsets.UTF_8)
            
            shareFile(context, file, "application/vnd.ms-excel", "مشاركة تقرير Excel")
        } catch (e: Exception) {
            Log.e("EXCEL_EXPORT", "Error in exportToExcel", e)
            Toast.makeText(context, "حدث خطأ أثناء تصدير Excel: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Exports report data to a native PDF file utilizing Android's Canvas drawing APIs.
     * Supports pagination, auto-wrapping, professional header titles, and customized styles.
     */
    fun exportToPdf(context: Context, fileName: String, title: String, headers: List<String>, rows: List<List<String>>) {
        try {
            val pdfDocument = android.graphics.pdf.PdfDocument()
            
            // A4 Page dimension standard (595 x 842 points)
            val pageWidth = 595
            val pageHeight = 842
            val margin = 30f
            val tableWidth = pageWidth - (margin * 2)
            
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas
            
            // Paints
            val titlePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#DC2626")
                textSize = 16f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val datePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#475569")
                textSize = 8f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val headerPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 9f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val headerBgPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#1E293B")
            }
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 8f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val borderPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#CBD5E1")
                strokeWidth = 0.5f
                style = android.graphics.Paint.Style.STROKE
            }
            val zebraPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#F8FAFC")
            }
            
            // Draw title and subtitle
            canvas.drawText(title, pageWidth / 2f, 50f, titlePaint)
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            canvas.drawText("تاريخ الإصدار: $dateStr", pageWidth / 2f, 70f, datePaint)
            
            val startY = 100f
            val rowHeight = 22f
            val colCount = headers.size
            val colWidth = tableWidth / colCount
            
            // Draw header bar
            var currentY = startY
            canvas.drawRect(margin, currentY, margin + tableWidth, currentY + rowHeight, headerBgPaint)
            
            for (i in 0 until colCount) {
                // Arabic rendering layout helper (visually draw right-to-left)
                val visualIndex = colCount - 1 - i
                val cx = margin + (visualIndex * colWidth) + (colWidth / 2f)
                canvas.drawText(headers[i], cx, currentY + 14f, headerPaint)
                canvas.drawLine(margin + (i * colWidth), currentY, margin + (i * colWidth), currentY + rowHeight, borderPaint)
            }
            canvas.drawLine(margin + tableWidth, currentY, margin + tableWidth, currentY + rowHeight, borderPaint)
            canvas.drawLine(margin, currentY, margin + tableWidth, currentY, borderPaint)
            canvas.drawLine(margin, currentY + rowHeight, margin + tableWidth, currentY + rowHeight, borderPaint)
            
            currentY += rowHeight
            
            var rowIdx = 0
            for (row in rows) {
                // Pagination check (if drawing overflows page, commit page and create a new page)
                if (currentY + rowHeight > pageHeight - 50) {
                    pdfDocument.finishPage(page)
                    val newPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create()
                    page = pdfDocument.startPage(newPageInfo)
                    canvas = page.canvas
                    currentY = 50f
                    
                    // Redraw Table Header on new page
                    canvas.drawRect(margin, currentY, margin + tableWidth, currentY + rowHeight, headerBgPaint)
                    for (i in 0 until colCount) {
                        val visualIndex = colCount - 1 - i
                        val cx = margin + (visualIndex * colWidth) + (colWidth / 2f)
                        canvas.drawText(headers[i], cx, currentY + 14f, headerPaint)
                        canvas.drawLine(margin + (i * colWidth), currentY, margin + (i * colWidth), currentY + rowHeight, borderPaint)
                    }
                    canvas.drawLine(margin + tableWidth, currentY, margin + tableWidth, currentY + rowHeight, borderPaint)
                    canvas.drawLine(margin, currentY, margin + tableWidth, currentY, borderPaint)
                    canvas.drawLine(margin, currentY + rowHeight, margin + tableWidth, currentY + rowHeight, borderPaint)
                    currentY += rowHeight
                }
                
                // Zebra row styling
                if (rowIdx % 2 == 1) {
                    canvas.drawRect(margin, currentY, margin + tableWidth, currentY + rowHeight, zebraPaint)
                }
                
                // Draw row columns
                for (i in 0 until colCount) {
                    val visualIndex = colCount - 1 - i
                    val cx = margin + (visualIndex * colWidth) + (colWidth / 2f)
                    val cellText = row.getOrNull(i) ?: ""
                    canvas.drawText(cellText, cx, currentY + 14f, textPaint)
                    canvas.drawLine(margin + (i * colWidth), currentY, margin + (i * colWidth), currentY + rowHeight, borderPaint)
                }
                canvas.drawLine(margin + tableWidth, currentY, margin + tableWidth, currentY + rowHeight, borderPaint)
                canvas.drawLine(margin, currentY + rowHeight, margin + tableWidth, currentY + rowHeight, borderPaint)
                
                currentY += rowHeight
                rowIdx++
            }
            
            pdfDocument.finishPage(page)
            
            val file = File(context.cacheDir, "${fileName}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.pdf")
            pdfDocument.writeTo(file.outputStream())
            pdfDocument.close()
            
            shareFile(context, file, "application/pdf", "مشاركة تقرير PDF")
        } catch (e: Exception) {
            Log.e("PDF_EXPORT", "Error in exportToPdf", e)
            Toast.makeText(context, "فشل تصدير ملف PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Helper to generate HTML formatted document representation of tabular reports.
     */
    private fun generateHtmlTable(title: String, headers: List<String>, rows: List<List<String>>): String {
        val htmlBuilder = java.lang.StringBuilder()
        htmlBuilder.append("<!DOCTYPE html><html><head>")
        htmlBuilder.append("<style>")
        htmlBuilder.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; direction: rtl; padding: 20px; background-color:#FAFAFA; color:#333; }")
        htmlBuilder.append(".header { text-align: center; margin-bottom: 30px; }")
        htmlBuilder.append(".header h1 { color: #DC2626; margin: 5px 0; }")
        htmlBuilder.append(".header p { color: #666; font-size:14px; margin: 5px 0; }")
        htmlBuilder.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); background:#FFF; }")
        htmlBuilder.append("th, td { border: 1px solid #CBD5E1; padding: 12px 10px; text-align: center; }")
        htmlBuilder.append("th { background-color: #1E293B; color: white; font-weight: bold; }")
        htmlBuilder.append("tr:nth-child(even) { background-color: #F8FAFC; }")
        htmlBuilder.append(".footer { margin-top: 40px; text-align: center; font-size: 11px; color: #94A3B8; border-top: 1px solid #E2E8F0; padding-top: 15px; }")
        htmlBuilder.append("</style>")
        htmlBuilder.append("</head><body>")
        
        htmlBuilder.append("<div class='header'>")
        htmlBuilder.append("<h1>$title</h1>")
        htmlBuilder.append("<p>تاريخ وتوقيت التقرير: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}</p>")
        htmlBuilder.append("</div>")
        
        htmlBuilder.append("<table>")
        
        // Headers
        htmlBuilder.append("<tr>")
        for (header in headers) {
            htmlBuilder.append("<th>$header</th>")
        }
        htmlBuilder.append("</tr>")
        
        // Rows
        for (row in rows) {
            htmlBuilder.append("<tr>")
            for (cell in row) {
                htmlBuilder.append("<td>$cell</td>")
            }
            htmlBuilder.append("</tr>")
        }
        
        htmlBuilder.append("</table>")
        htmlBuilder.append("<div class='footer'>هذا التقرير تم توليده تلقائياً بواسطة نظام إدارة ميكروتك والموزع الذكي</div>")
        htmlBuilder.append("</body></html>")
        return htmlBuilder.toString()
    }

    private fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        val authority = "${context.packageName}.fileprovider"
        val fileUri: Uri = FileProvider.getUriForFile(context, authority, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, chooserTitle)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
