package demo.cocodiy.model;

import java.util.ArrayList;
import java.util.List;

public class Cat {
    private String title;
    private String path;
    private List<Cat> childs=new ArrayList<>();

    public Cat() {
    }

    public Cat(String title) {
        this.title = title;
    }

    public Cat(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Cat> getChilds() {
        return childs;
    }

    public void addChild(Cat child) {
        this.childs.add(child);
    }
}
