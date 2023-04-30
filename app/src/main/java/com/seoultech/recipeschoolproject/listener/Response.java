package com.seoultech.recipeschoolproject.listener;

// 응답 객체의 데이터는 제네릭 타입(T)으로 정의되어 있으므로, 어떤 종류의 데이터든 담을 수 있습니다.
// 이를 통해 서버에서 클라이언트로 다양한 데이터 타입의 응답 객체를 보내어
// 클라이언트에서 데이터를 적절하게 처리할 수 있습니다.
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