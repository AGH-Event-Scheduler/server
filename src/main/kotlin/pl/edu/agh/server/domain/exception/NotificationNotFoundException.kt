package pl.edu.agh.server.domain.exception

class NotificationNotFoundException(notificationId: Long) :
    RuntimeException("Notification with ID $notificationId not found")
