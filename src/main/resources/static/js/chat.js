function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    const errorMessageDiv = document.querySelector('.invalid-feedback');

    if (messageContent && stompClient) {
        const chatMessage = {
            messageType: 'CHAT',
            content: messageInput.value,
            user: {
                id: userId,
                username: username,
            },
            project: {
                id: projectId
            }
        };
        stompClient.send(`/app/${projectId}/sendMessage`, {}, JSON.stringify(chatMessage));

        // Clear input value
        messageInput.value = '';

        // Remove error message
        errorMessageDiv.textContent = '';
    } else {
        // If the message content is empty, display the error message
        errorMessageDiv.textContent = 'Please enter your message';
    }

    event.preventDefault();
}
