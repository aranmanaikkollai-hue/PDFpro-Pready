package com.propdf.editor.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.propdf.editor.domain.repository.PdfOperationsRepository
import com.propdf.editor.utils.FileUtils
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfBoxOperations @Inject constructor(
    private val context: Context
) : PdfOperationsRepository {

    init {
        PDFBoxResourceLoader.init(context)
    }

    override suspend fun mergePdfs(files: List<Uri>, outputFile: File): Result<File> =
        withContext(Dispatchers.IO) {
            runCatching {
                val mergedDoc = PDDocument()
                files.forEach { uri ->
                    val tempFile = FileUtils.uriToTempFile(context, uri)
                        ?: throw Exception("Cannot read file: $uri")
                    PDDocument.load(tempFile).use { doc ->
                        doc.pages.forEach { page ->
                            mergedDoc.addPage(page)
                        }
                    }
                }
                mergedDoc.save(outputFile)
                mergedDoc.close()
                outputFile
            }
        }

    override suspend fun splitPdf(
        file: Uri,
        pageRanges: List<IntRange>,
        outputDir: File
    ): Result<List<File>> = withContext(Dispatchers.IO) {
        runCatching {
            val tempFile = FileUtils.uriToTempFile(context, file)
                ?: throw Exception("Cannot read file")

            val sourceDoc = PDDocument.load(tempFile)
            val outputFiles = mutableListOf<File>()

            pageRanges.forEachIndexed { index, range ->
                val newDoc = PDDocument()
                range.forEach { pageNum ->
                    if (pageNum in 0 until sourceDoc.numberOfPages) {
                        newDoc.addPage(sourceDoc.getPage(pageNum))
                    }
                }
                val outFile = File(outputDir, "split_${index + 1}.pdf")
                newDoc.save(outFile)
                newDoc.close()
                outputFiles.add(outFile)
            }
            sourceDoc.close()
            outputFiles
        }
    }

    override suspend fun compressPdf(file: Uri, outputFile: File, quality: Int): Result<File> =
        withContext(Dispatchers.IO) {
            runCatching {
                val tempFile = FileUtils.uriToTempFile(context, file)
                    ?: throw Exception("Cannot read file")

                PDDocument.load(tempFile).use { doc ->
                    // Compress images in the document
                    doc.pages.forEach { page ->
                        val resources = page.resources
                        // Image compression logic would go here
                        // For now, we use basic save with compression
                    }
                    doc.save(outputFile)
                }
                outputFile
            }
        }

    override suspend fun rotatePages(
        file: Uri,
        outputFile: File,
        rotations: Map<Int, Int>
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val tempFile = FileUtils.uriToTempFile(context, file)
                ?: throw Exception("Cannot read file")

            PDDocument.load(tempFile).use { doc ->
                rotations.forEach { (pageNum, degrees) ->
                    if (pageNum in 0 until doc.numberOfPages) {
                        val page = doc.getPage(pageNum)
                        page.rotation = (page.rotation + degrees) % 360
                    }
                }
                doc.save(outputFile)
            }
            outputFile
        }
    }

    override suspend fun deletePages(
        file: Uri,
        outputFile: File,
        pagesToDelete: List<Int>
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val tempFile = FileUtils.uriToTempFile(context, file)
                ?: throw Exception("Cannot read file")

            PDDocument.load(tempFile).use { doc ->
                val sortedPages = pagesToDelete.sortedDescending()
                sortedPages.forEach { pageNum ->
                    if (pageNum in 0 until doc.numberOfPages) {
                        doc.removePage(pageNum)
                    }
                }
                doc.save(outputFile)
            }
            outputFile
        }
    }

    override suspend fun addWatermark(file: Uri, outputFile: File, text: String): Result<File> =
        withContext(Dispatchers.IO) {
            runCatching {
                val tempFile = FileUtils.uriToTempFile(context, file)
                    ?: throw Exception("Cannot read file")

                PDDocument.load(tempFile).use { doc ->
                    doc.pages.forEach { page ->
                        val contentStream = PDPageContentStream(
                            doc, page, PDPageContentStream.AppendMode.APPEND, true
                        )
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 48f)
                        contentStream.setNonStrokingColor(200, 200, 200)
                        val mediaBox = page.mediaBox
                        contentStream.beginText()
                        contentStream.newLineAtOffset(
                            mediaBox.width / 4,
                            mediaBox.height / 2
                        )
                        contentStream.showText(text)
                        contentStream.endText()
                        contentStream.close()
                    }
                    doc.save(outputFile)
                }
                outputFile
            }
        }

    override suspend fun encryptPdf(
        file: Uri,
        outputFile: File,
        password: String
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val tempFile = FileUtils.uriToTempFile(context, file)
                ?: throw Exception("Cannot read file")

            PDDocument.load(tempFile).use { doc ->
                val accessPermission = AccessPermission().apply {
                    setCanPrint(true)
                    setCanModify(false)
                    setCanExtractContent(true)
                }
                val protectionPolicy = StandardProtectionPolicy(
                    password, password, accessPermission
                ).apply {
                    encryptionKeyLength = 128
                }
                doc.protect(protectionPolicy)
                doc.save(outputFile)
            }
            outputFile
        }
    }

    override suspend fun imagesToPdf(images: List<Uri>, outputFile: File): Result<File> =
        withContext(Dispatchers.IO) {
            runCatching {
                val document = PDDocument()
                images.forEach { uri ->
                    val tempFile = FileUtils.uriToTempFile(context, uri)
                        ?: return@forEach

                    val bitmap = android.graphics.BitmapFactory.decodeFile(tempFile.absolutePath)
                        ?: return@forEach

                    val pageWidth = bitmap.width.toFloat()
                    val pageHeight = bitmap.height.toFloat()
                    val page = PDPage(PDRectangle(pageWidth, pageHeight))
                    document.addPage(page)

                    val pdImage = JPEGFactory.createFromImage(document, bitmap, 0.8f)
                    val contentStream = PDPageContentStream(document, page)
                    contentStream.drawImage(pdImage, 0f, 0f, pageWidth, pageHeight)
                    contentStream.close()
                    bitmap.recycle()
                }
                document.save(outputFile)
                document.close()
                outputFile
            }
        }

    override suspend fun pdfToImages(file: Uri, outputDir: File): Result<List<File>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val tempFile = FileUtils.uriToTempFile(context, file)
                    ?: throw Exception("Cannot read file")

                val outputFiles = mutableListOf<File>()
                PDDocument.load(tempFile).use { doc ->
                    val pdfRenderer = com.tom_roush.pdfbox.rendering.PDFRenderer(doc)
                    for (pageIndex in 0 until doc.numberOfPages) {
                        val bitmap = pdfRenderer.renderImageWithDPI(
                            pageIndex, 200f,
                            com.tom_roush.pdfbox.rendering.ImageType.RGB
                        )
                        val outFile = File(outputDir, "page_${pageIndex + 1}.png")
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outFile.outputStream())
                        outputFiles.add(outFile)
                    }
                }
                outputFiles
            }
        }

    override suspend fun extractText(file: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val tempFile = FileUtils.uriToTempFile(context, file)
                ?: throw Exception("Cannot read file")

            PDDocument.load(tempFile).use { doc ->
                val stripper = PDFTextStripper()
                stripper.getText(doc)
            }
        }
    }
}
