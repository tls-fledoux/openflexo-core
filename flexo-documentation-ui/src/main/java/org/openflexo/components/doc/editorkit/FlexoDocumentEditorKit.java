package org.openflexo.components.doc.editorkit;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

import org.openflexo.components.doc.editorkit.view.DocxViewFactory;

/**
 * This is the implementation of editing functionality.
 *
 * Note that this class was originally inspired from Stanislav Lapitsky code (see http://java-sl.com/docx_editor_kit.html)
 * 
 * @author Stanislav Lapitsky
 * @author Sylvain Guerin
 */
public class FlexoDocumentEditorKit extends StyledEditorKit {

	/**
	 * Creates instance of docx editor kit.
	 */
	public FlexoDocumentEditorKit() {
		super();
	}

	/**
	 * Create a copy of the editor kit. This allows an implementation to serve as a prototype for others, so that they can be quickly
	 * created.
	 *
	 * @return the copy
	 */
	@Override
	public Object clone() {
		return new FlexoDocumentEditorKit();
	}

	/**
	 * Get the MIME type of the data that this kit represents support for. This kit supports the type.
	 *
	 * @return the type
	 */
	@Override
	public String getContentType() {
		return "text/rtf";// "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; // TODO check the type
		// return "application/x-java-text-encoding"; //TODO check the type
	}

	/**
	 * Insert content from the given stream which is expected to be in a format appropriate for this kind of content handler.
	 *
	 * @param in
	 *            The stream to read from
	 * @param doc
	 *            The destination for the insertion.
	 * @param pos
	 *            The location in the document to place the content.
	 * @exception IOException
	 *                on any I/O error
	 * @exception BadLocationException
	 *                if pos represents an invalid location within the document.
	 */
	@Override
	public void read(InputStream in, Document doc, int pos) throws IOException, BadLocationException {
		// FlexoDocumentReader db = new FlexoDocumentReader(doc);
		System.out.println("Hop un nouveau reader");
		// db.read(in, pos);
	}

	/**
	 * Insert content from the given stream, which will be treated as plain text.
	 *
	 * @param in
	 *            The stream to read from
	 * @param doc
	 *            The destination for the insertion.
	 * @param pos
	 *            The location in the document to place the content.
	 * @exception IOException
	 *                on any I/O error
	 * @exception BadLocationException
	 *                if pos represents an invalid location within the document.
	 */
	@Override
	public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {

		BufferedReader br = new BufferedReader(in);

		String s = br.readLine();
		StringBuilder sb = new StringBuilder();
		while (s != null) {
			sb.append(s).append("\n");
			s = br.readLine();
		}
		System.out.println(sb.toString());
		read(new ByteArrayInputStream(sb.toString().getBytes("UTF-8")), doc, pos);
	}

	/**
	 * Write content from a document to the given stream in a format appropriate for this kind of content handler.
	 *
	 * @param out
	 *            The stream to write to
	 * @param doc
	 *            The source for the write.
	 * @param pos
	 *            The location in the document to fetch the content.
	 * @param len
	 *            The amount to write out.
	 * @exception IOException
	 *                on any I/O error
	 * @exception BadLocationException
	 *                if pos represents an invalid location within the document.
	 */
	@Override
	public void write(OutputStream out, Document doc, int pos, int len) throws IOException, BadLocationException {
		// DocxWriter writer = new DocxWriter(doc);
		// writer.write(out, pos, len);
	}

	/**
	 * Write content from a document to the given stream as plain text.
	 *
	 * @param out
	 *            The stream to write to
	 * @param doc
	 *            The source for the write.
	 * @param pos
	 *            The location in the document to fetch the content.
	 * @param len
	 *            The amount to write out.
	 * @exception IOException
	 *                on any I/O error
	 * @exception BadLocationException
	 *                if pos represents an invalid location within the document.
	 */
	@Override
	public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {

		throw new BadLocationException("Not implemented!", 0);
	}

	/**
	 * Write content from a document to the given file as plain text.
	 *
	 * @param fileName
	 *            ?out? The name of source file to write to
	 * @param doc
	 *            The source for the write.
	 * @exception IOException
	 *                on any I/O error
	 * @exception BadLocationException
	 *                if pos represents an invalid location within the document.
	 */
	public void write(String fileName, Document doc) throws IOException, BadLocationException {
		// DocxWriter writer = new DocxWriter(doc);
		// writer.write(fileName);
	}

	/**
	 * Create an uninitialized text storage model that is appropriate for this type of editor.
	 *
	 * @return the model
	 */
	@Override
	public Document createDefaultDocument() {
		FlexoStyledDocument doc = new FlexoStyledDocument(null);
		return doc;
	}

	/**
	 * Fetch a factory that is suitable for producing views of any models that are produced by this kit.
	 *
	 * @return the factory
	 */
	@Override
	public ViewFactory getViewFactory() {
		return new DocxViewFactory();
	}

}
