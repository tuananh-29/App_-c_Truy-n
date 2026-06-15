package com.example.truyenmoingay.activities;

import androidx.lifecycle.ViewModel;

public class ReaderViewModel extends ViewModel {
    private int scrollPosition = 0;
    private int scrollOffset = 0;

    public int getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }
}