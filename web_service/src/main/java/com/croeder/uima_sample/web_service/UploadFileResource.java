package com.croeder.uima_sample.web_service;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
//import javax.ws.rs.FormDataParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;


// pretty much a copy of : http://www.mkyong.com/webservices/jax-rs/file-upload-example-in-jersey/

@Path("file")
public class UploadFileResource {
	String errorMessage;

    @POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {


		System.out.println("POST UploadFileResource:"); 

		File baseDir = new File("/Users/roederc/storage"); // TODO property
		File uploadedFile = new File(baseDir, fileDetail.getFileName());
		int retval = writeToFile(uploadedInputStream, uploadedFile);
		int returnCode=0;
		switch (retval) {
		case 0:
			errorMessage="File uploaded to "  + uploadedFile.getAbsolutePath();	
			returnCode=201;
		case -1:
			returnCode=500;
		case -2:
			returnCode=500;
		}

		return Response.status(returnCode).entity(errorMessage).build();
    }


	private int writeToFile(InputStream inputStream,
		File uploadedFile) {
	
		OutputStream out = null;
		try {
			out = new FileOutputStream(uploadedFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
		}
		catch (FileNotFoundException x) {
			System.out.println("error:" + x);
			errorMessage="Error, coulnd't write to: "  + uploadedFile.getAbsolutePath() + " " + x;	
			x.printStackTrace();
			return -1;
		}
		catch (IOException x) {
			System.out.println("error:" + x);
			errorMessage="Error, unknown IOException:" + x;
			x.printStackTrace();
			return -2;
		}
		finally {
			try {
				out.flush();
				out.close();
			}
			catch (IOException x) {
				// eat
			}
		}
		return 0;
	}


}
