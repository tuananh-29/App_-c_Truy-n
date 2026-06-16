package com.example.truyenmoingay.viewmodels;

import androidx.lifecycle.ViewModel;
import com.example.truyenmoingay.models.Comic;
import java.util.List;

public class ExploreViewModel extends ViewModel {

    private String searchKeyword = "";
    private List<Comic> filteredList;

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String keyword) {
        this.searchKeyword = keyword;
    }

    public List<Comic> getFilteredList() {
        return filteredList;
    }

    public void setFilteredList(List<Comic> list) {
        this.filteredList = list;
    }
}