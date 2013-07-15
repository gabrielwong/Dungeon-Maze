package maze;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class SaveImageTask extends SwingWorker{
	private final RenderedImage image;
	private final File path;
	private final String format;

	public SaveImageTask(RenderedImage image, File path, String format){
		this.image = image;
		this.path = path;
		this.format = format;
	}

	private void saveImage(){
		IIOWriteProgressListener listener = new IIOWriteProgressListener(){
			public void imageComplete(ImageWriter arg0) {
				setProgress(100);
			}
			public void imageProgress(ImageWriter arg0, float arg1) {
				setProgress((int)arg1);
			}
			public void imageStarted(ImageWriter arg0, int arg1) {}
			public void thumbnailComplete(ImageWriter arg0) {}
			public void thumbnailProgress(ImageWriter arg0, float arg1) {}
			public void thumbnailStarted(ImageWriter arg0, int arg1, int arg2) {}
			public void writeAborted(ImageWriter arg0) {}

		};

		ImageWriter writer = ImageIO.getImageWritersBySuffix(format).next();
		FileImageOutputStream stream;
		try {
			stream = new FileImageOutputStream(path);
			writer.setOutput(stream);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(),
					e1.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE); // show error dialog
			return;
		}
		
		writer.addIIOWriteProgressListener(listener);
		
		try {
			writer.write(image);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(),
					e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE); // show error dialog
		}
		try {
			stream.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(),
					e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE); // show error dialog
		}
	}

	@Override
	protected Object doInBackground() throws Exception {
		saveImage();
		return null;
	}

}
