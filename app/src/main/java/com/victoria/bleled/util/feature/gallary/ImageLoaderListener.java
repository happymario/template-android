package com.victoria.bleled.util.feature.gallary;

import java.util.List;

public interface ImageLoaderListener {
    void onImageLoaded(List<Gallary> images, List<Folder> folders);

    void onFailed(Throwable throwable);
}
