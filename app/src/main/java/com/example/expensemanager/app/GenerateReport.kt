package com.example.expensemanager.app

import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import android.content.Context
import java.io.File

class GenerateReport(private val context: Context) {

    fun createPdf(fileName: String, headerData: HeaderData, summaryData: SummaryData, expenseDetails: List<ExpenseDetail>) {
        val filePath = File(context.filesDir, "$fileName.pdf")
        val pdfWriter = PdfWriter(filePath)
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Add Header
        document.add(Paragraph(headerData.title).setBold().setFontSize(18f))
        document.add(Paragraph("Company: ${headerData.companyName}").setFontSize(12f))
        document.add(Paragraph("Date Range: ${headerData.dateRange}").setFontSize(12f))

        // Add Summary
        document.add(Paragraph("\nSummary:").setBold().setFontSize(16f))
        document.add(Paragraph("Total Expenses: ₹${summaryData.totalExpenses}"))
        document.add(Paragraph("Total Reimbursable: ₹${summaryData.totalReimbursable}"))
        document.add(Paragraph("\nCategory Breakdown:").setFontSize(14f))
        summaryData.categoryBreakdown.forEach { (category, amount) ->
            document.add(Paragraph("$category: ₹$amount"))
        }

        // Add Expense Table
        document.add(Paragraph("\nExpense Details:").setBold().setFontSize(16f))
        val table = Table(floatArrayOf(1f, 2f, 2f, 1f, 1f, 1f))
        table.addCell(Cell().add(Paragraph("Date")))
        table.addCell(Cell().add(Paragraph("Category")))
        table.addCell(Cell().add(Paragraph("Description")))
        table.addCell(Cell().add(Paragraph("Amount")))
        table.addCell(Cell().add(Paragraph("Payment Method")))
        table.addCell(Cell().add(Paragraph("Reimbursable")))

        expenseDetails.forEach {
            table.addCell(Cell().add(Paragraph(it.date)))
            table.addCell(Cell().add(Paragraph(it.category)))
            table.addCell(Cell().add(Paragraph(it.description)))
            table.addCell(Cell().add(Paragraph("₹${it.amount}")))

        }

        document.add(table)

        // Close Document
        document.close()
    }
}

data class HeaderData(val title: String, val companyName: String, val dateRange: String)
data class SummaryData(val totalExpenses: Double, val totalReimbursable: Double, val categoryBreakdown: Map<String, Any>)
data class ExpenseDetail(val date: String, val category: String, val description: String, val amount: Double)