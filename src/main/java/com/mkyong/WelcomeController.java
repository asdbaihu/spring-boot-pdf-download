package com.mkyong;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
public class WelcomeController {

	// inject via application.properties
	@Value("${welcome.message:test}")
	private String message = "Hello World";

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}

	@RequestMapping("show-file-{id}")
	public ResponseEntity<byte[]> getPdf(@PathVariable("id") String empId) throws DocumentException,
			IOException {
		String fileName = String.valueOf(System.currentTimeMillis()) +"-"+empId+ ".pdf";

		Document document = new Document();
		@SuppressWarnings("unused")
		PdfWriter pdfWriter = PdfWriter.getInstance(document,
				new FileOutputStream(fileName));
		document.open();

		Paragraph paragraph = new Paragraph();
		paragraph.add("Employee Id: "+empId);

		document.add(paragraph);
		document.close();

		Path path = Paths.get(fileName);
		byte[] contents = Files.readAllBytes(path);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));

		headers.setContentDispositionFormData(fileName, fileName);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents,
				headers, HttpStatus.OK);
		return response;
	}

}