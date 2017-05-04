package com.google.firebase.udacity.receiptapp.features.capture;

import java.io.File;

abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}
