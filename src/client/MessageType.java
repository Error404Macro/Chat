package client;

public enum MessageType {
    // тип сообщений клиент-сервер
    // новый клиент = запрос имени и его отправка пользователем
    // получив имя -> принять его или запросить новое
    // новый клиент -> сообщение другим участникам о добавлении, удалении
    // сообщение -> переслать всем другим участникам
    NAME_REQUEST,
    USER_NAME,
    NAME_ACCEPTED,
    TEXT,
    USER_ADDED,
    USER_REMOVED
}
