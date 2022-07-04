package com.seoultech.recipeschoolproject.listener;

public class Response<T> {
    private Type type;
    private T data;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isEmpty() {
        return data == null;
    }

    public boolean isNotEmpty() {
        return data != null;
    }
}
