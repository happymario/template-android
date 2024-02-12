package com.mario.template.helper.gallary;

import java.util.List;

public interface ImageLoaderListener {
    void onImageLoaded(List<Gallary> images, List<Folder> folders);

    void onFailed(Throwable throwable);
}
