package chasqui.services.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.springframework.beans.factory.annotation.Autowired;

import chasqui.model.Imagen;

public class FileSaver {

	@Autowired
	String serverRelativePath;
	
	
	public Imagen guardarImagen(String picturesAbsolutePath, String username, String fileName, byte[] bytes){
		try{
			//Crear directorio del vendedor
			String barra = (picturesAbsolutePath.endsWith("/"))?"":"/";
			String userDirPath=picturesAbsolutePath+barra+"usuarios/"+username;
			File dir = new File(userDirPath);
			dir.mkdir();
			
			//Guardando archivo en el file system
			String fileNameConPath = userDirPath+"/"+fileName;
			File f = new File(fileNameConPath);
			f.setWritable(true);
			f.getParentFile().mkdirs();
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bytes);
			fos.flush();
			fos.close();
			f.setWritable(false);
			
			//Creando objeto para persistir en BD
			Imagen imagen = new Imagen();
			String imageFilePath = serverRelativePath+username+"/"+fileName;
			imagen.setPath(imageFilePath);
			imagen.setAbsolutePath(fileNameConPath);
			//imagen.setAbsolutePath(fileNameConPath);
			imagen.setNombre(fileName);
			return imagen;			
		}catch(Exception e){
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public Imagen guardarImagenConPathRelativeDinamico(String picturesAbsolutePath,String relativePath, String username, String fileName, byte[] bytes){
		try{
			//Crear directorio del vendedor
			String barra = (picturesAbsolutePath.endsWith("/"))?"":"/";
			String userDirPath=picturesAbsolutePath+barra+"usuarios/"+username;
			File dir = new File(userDirPath);
			dir.mkdir();
			
			//Guardando archivo en el file system
			String fileNameConPath = userDirPath+"/"+fileName;
			File f = new File(fileNameConPath);
			f.setWritable(true);
			f.getParentFile().mkdirs();
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bytes);
			fos.flush();
			fos.close();
			f.setWritable(false);
			
			//Creando objeto para persistir en BD
			Imagen imagen = new Imagen();
			String imageFilePath = relativePath+username+"/"+fileName;
			imagen.setPath(imageFilePath);
			imagen.setAbsolutePath(fileNameConPath);
			//imagen.setAbsolutePath(fileNameConPath);
			imagen.setNombre(fileName);
			return imagen;			
		}catch(Exception e){
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void borrarImagenEnCarpeta(String serverAbsolutePath) {
		File file = new File(serverAbsolutePath);
		file.delete();
	}

	
	public Imagen recuperarImagen(String serverAbsolutePath,String username,String fileName){
		try{
			Imagen imagen = new Imagen();
			String relativeFileName = serverRelativePath+username+"/"+fileName;
			//String absoluteFileName = serverAbsolutePath+username+"/"+fileName;
			
			imagen.setPath(relativeFileName);
			//imagen.setAbsolutePath(absoluteFileName);
			imagen.setNombre(fileName);
			return imagen;			
		}catch(Exception e){
			throw new RuntimeException(e.getMessage());
		}
	}
	public byte[] iconizar(InputStream contenido,String extension) throws IOException{
		BufferedImage content = ImageIO.read(contenido);
		BufferedImage scaledImg = Scalr.resize(content, Method.QUALITY, 
                16, 16, Scalr.OP_ANTIALIAS);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( scaledImg, extension, baos );
		baos.flush();
		return baos.toByteArray();
	}
	
	
}
