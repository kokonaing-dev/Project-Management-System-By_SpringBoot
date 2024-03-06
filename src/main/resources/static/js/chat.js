
function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        const chatMessage = {
            messageType: 'CHAT',
            content: messageInput.value,
            user: {
                id: userId,
                username: username,
            },
            project: {
                id : projectId
            }
        };
        stompClient.send(`/app/${projectId}/sendMessage`, {}, JSON.stringify(chatMessage));

        messageInput.value = '';
    }
    event.preventDefault();
}