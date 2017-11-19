package cn.edu.iip.nju.common;

import java.util.List;

/**
 * 手写分页
 * Created by xu on 2017/10/25.
 */

public class PageHelper<T> {

    private int pageSize = 40;      //默认一页的大小
    private int totalRows;          //总的记录数
    private int totalPages;         //总页数
    private int current;            //当前页码(从1开始！！！！！)
    private int beginIndex;         //当前页的第一条记录的index
    private int endIndex;           //当前页的最后一条记录的index
    private boolean isFirst;        //当前页是否是第一页
    private boolean isLast;         //当前页是否是最后一页
    private boolean hasPreviousPage;//当前页是否是第一页
    private boolean hasNextPage;    //当前页是否是最后一页
    private List<T> content;        //当前页的数据

    public PageHelper(int current, int totalRows) {
        this.current = current;
        this.totalRows = totalRows;

        if (totalRows % pageSize == 0 && totalRows!=0) {
            this.totalPages = totalRows / pageSize;
        } else {
            this.totalPages = totalRows / pageSize + 1;
        }

        this.beginIndex = (current - 1) * pageSize;
        if(current==totalPages){
            this.endIndex = totalRows;
        }else {
            this.endIndex = beginIndex + pageSize;
        }
        this.isFirst = current == 1;
        this.isLast = current == totalPages;

        this.hasPreviousPage = current > 1;
        this.hasNextPage = current<totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrent() {
        return current;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public boolean isLast() {
        return isLast;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
