package com.example.truyenmoingay;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class SearchViewModel extends ViewModel {
    // Biến lưu từ khóa tìm kiếm (Giữ trạng thái khi xoay màn hình)
    private String searchKeyword = "";

    // Danh sách truyện tìm kiếm được
    private final MutableLiveData<List<String>> searchResults = new MutableLiveData<>();

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public LiveData<List<String>> getSearchResults() {
        return searchResults;
    }
}