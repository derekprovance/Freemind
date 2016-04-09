package freemind.view;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.ImageIcon;

import freemind.main.Resources;
import freemind.main.Tools;

public class ImageFactory {
	private static ImageFactory mInstance = null;

	public static ImageFactory getInstance() {
		if (mInstance == null) {
			mInstance = new ImageFactory();
		}
		return mInstance;
	}

	public ImageIcon createIcon(URL pUrl){
		if(Tools.getScalingFactorPlain()==100){
			return createUnscaledIcon(pUrl);
		}
		ScalableImageIcon icon = new ScalableImageIcon(pUrl);
		icon.setScale(Tools.getScalingFactor());
		return icon;
	}

	/**
	 * All icons directly displayed in the mindmap view are scaled by the zoom.
	 */
	public ImageIcon createUnscaledIcon(URL pResource) {
		return new ImageIcon(pResource);
	}

	public ImageIcon createIcon(String pFilePath) {
		if(Tools.getScalingFactorPlain()==200){
			if(pFilePath.endsWith(".png")){
				try {
					URL url = Resources.getInstance().getResource(pFilePath.replaceAll(".png$", "_32.png"));
					URLConnection connection = url.openConnection();
					if(connection.getContentLength()>0){
						return createUnscaledIcon(url);
					}
				} catch (IOException e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
		}
		return createIcon(Resources.getInstance().getResource(pFilePath));
	}
}
