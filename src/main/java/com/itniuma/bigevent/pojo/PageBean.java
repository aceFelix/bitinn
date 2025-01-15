package com.itniuma.bigevent.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//分页返回结果对象
public class PageBean <T>{
    private Long total;//总条数
    private List<T> items;//当前页数据集合


    public PageBean() {
    }

    public PageBean(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    /**
     * 获取
     * @return total
     */
    public Long getTotal() {
        return total;
    }

    /**
     * 设置
     * @param total
     */
    public void setTotal(Long total) {
        this.total = total;
    }

    /**
     * 获取
     * @return items
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * 设置
     * @param items
     */
    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "PageBean{total = " + total + ", items = " + items + "}";
    }
}
