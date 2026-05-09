package com.propdf.editor.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.propdf.editor.domain.repository.PdfOperationsRepository
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfBoxOperations @Inject constructor(
    @ApplicationContext private val context: Context
) : PdfOperationsRepository {

    init {
        PDFBoxResourceLoader.init(context)
    }

    override suspend fun mergePdfs(sources: List<Uri>, output: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            PDDocument().use { merged ->
                sources.forEach { uri ->
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        PDDocument.load(input).use { doc ->
                            doc.pages.forEach { page -> merged.addPage(page) }
                        }
                    }
                }
                context.contentResolver.openFileDescriptor(output, "w")?.use { pfd ->
                    FileOutputStream(pfd.fileDescriptor).use { out -> merged.save(out) }
                } ?: throw IllegalStateException("Cannot open output")
            }
        }
    }

    override suspend fun splitPdf(source: Uri, pages: List<Int>, output: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(source)?.use { input ->
                PDDocument.load(input).use { doc ->
                    PDDocument().use { split ->
                        pages.forEach { idx ->
                            if (idx in 0 until doc.numberOfPages) {
                                split.addPage(doc.getPage(idx))
                            }
                        }
                        context.contentResolver.openFileDescriptor(output, "w")?.use { pfd ->
                            FileOutputStream(pfd.fileDescriptor).use { out -> split.save(out) }
                        } ?: throw IllegalStateException("Cannot open output")
                    }
                }
            } ?: throw IllegalStateException("Cannot open source")
        }
    }

    override suspend fun compressPdf(source: Uri, output: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(source)?.use { input ->
                PDDocument.load(input).use { doc ->
                    doc.isAllSecurityToBeRemoved = true
                    context.contentResolver.openFileDescriptor(output, "w")?.use { pfd ->
                        FileOutputStream(pfd.fileDescriptor).use { out -> doc.save(out) }
                    } ?: throw IllegalStateException("Cannot open output")
                }
            } ?: throw IllegalStateException("Cannot open source")
        }
    }

    override suspend fun rotatePages(source: Uri, pages: List<Int>, degrees: Int, output: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(source)?.use { input ->
                PDDocument.load(input).use { doc ->
                    pages.forEach { idx ->
                        if (idx in 0 until doc.numberOfPages) {
                            val page = doc.getPage(idx)
                            page.rotation = (page.rotation + degrees) % 360
                        }
                    }
                    context.contentResolver.openFileDescriptor(output, "w")?.use { pfd ->
                        FileOutputStream(pfd.fileDescriptor).use { out -> doc.save(out) }
                    } ?: throw IllegalStateException("Cannot open output")
                }
            } ?: throw IllegalStateException("Cannot open source")
        }
    }

    override suspend fun deletePages(source: Uri, pages: List<Int>, output: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(source)?.use { input ->
                PDDocument.load(input).use { doc ->
                    val toDelete = pages.filter { it in 0 until doc.numberOfPages }.sortedDescending()
                    toDelete.forEach { doc.removePage(it) }
                    context.contentResolver.openFileDescriptor(output, "w")?.use { pfd ->
                        FileOutputStream(pfd.fileDescriptor).use { out -> doc.save(out) }
                    } ?: throw IllegalStateException("Cannot open output")
                }
            } ?: throw IllegalStateException("Cannot open source")
        }
    }

    override suspend fun addWatermark(source: Uri, text: String, output: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(source)?.use { input ->
                PDDocument.load(input).use { doc ->
                    doc.pages.forEach { page ->
                        PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true).use { stream ->
                            stream.setFont(PDType1Font.HELVETICA_BOLD, 48f)
                            stream.setNonStrokingColor(200, 200, 200)
                            val rect = page.mediaBox
                            stream.beginText()
                            stream.newLineAtOffset(rect.width / 4, rect.height / 2)
                            stream.showText(text)
                            stream.endText()
                        }
                    }
                    context.contentResolver.openFileDescriptor(output, "w")?.use { pfd ->
                        FileOutputStream(pfd.fileDescriptor).use { out -> doc.save(out) }
                    } ?: throw IllegalStateException("Cannot open output")
                }
            } ?: throw IllegalStateException("Cannot open source")
        }
    }

    override suspend fun encryptPdf(source: Uri, password: String, output: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(source)?.use { input ->
                PDDocument.load(input).use { doc ->
                    val policy = StandardProtectionPolicy(password, password, AccessPermission().apply {
                        setCanPrint(true)
                        setCanModify(false)
                    })
                    policy.encryptionKeyLength = 128
                    doc.protect(policy)
                    context.contentResolver.openFileDescriptor(output, "w")?.use { pfd ->
                        FileOutputStream(pfd.fileDescriptor).use { out -> doc.save(out) }
                    } ?: throw IllegalStateException("Cannot open output")
                }
            } ?: throw IllegalStateException("Cannot open source")
        }
    }

    override suspend fun imagesToPdf(images: List<Uri>, output: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            PDDocument().use { doc ->
                images.forEach { uri ->
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        val bitmap = android.graphics.BitmapFactory.decodeStream(input)
                        val page = PDPage(PDRectangle.A4)
                        doc.addPage(page)
                        val img = PDImageXObject.createFromByteArray(doc, bitmapToBytes(bitmap), "image")
                        PDPageContentStream(doc, page).use { stream ->
                            val scale = 0.5f
                            stream.drawImage(img, 50f, 50f, img.width * scale, img.height * scale)
                        }
                        bitmap.recycle()
                    }
                }
                context.contentResolver.openFileDescriptor(output, "w")?.use { pfd ->
                    FileOutputStream(pfd.fileDescriptor).use { out -> doc.save(out) }
                } ?: throw IllegalStateException("Cannot open output")
            }
        }
    }

    override suspend fun pdfToImages(source: Uri, outputDir: Uri): Result<List<Uri>> = withContext(Dispatchers.IO) {
        runCatching {
            val results = mutableListOf<Uri>()
            context.contentResolver.openFileDescriptor(source, "r")?.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    val outDir = File(outputDir.path ?: context.cacheDir.absolutePath, "pdf_images").apply { mkdirs() }
                    for (i in 0 until renderer.pageCount) {
                        renderer.openPage(i).use { page ->
                            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            val outFile = File(outDir, "page_${i + 1}.jpg")
                            FileOutputStream(outFile).use { out ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                            }
                            results.add(Uri.fromFile(outFile))
                            bitmap.recycle()
                        }
                    }
                }
            }
            results
        }
    }

    override suspend fun extractText(source: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(source)?.use { input ->
                PDDocument.load(input).use { doc ->
                    PDFTextStripper().getText(doc)
                }
            } ?: throw IllegalStateException("Cannot open source")
        }
    }

    private fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
