'use strict';

const chatForm = document.getElementById('chat-form');
const connectingElement = document.querySelector('.connecting');
const messageInput = document.querySelector('#message');

let stompClient = null;
let username = null;
let userId = null;
let projectId = getProjectIdFromUrl();

async function connect() {
    try {
        // Make an API request to get the user information
        const response = await fetch('/api/userInfo');
        const userData = await response.json();

        // Check if the user is authenticated
        if (response.ok) {
            username = userData.username;
            userId = userData.id;

            const socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);

            stompClient.connect({}, onConnect, onError);
        } else {
            // Handle authentication error
            console.error('Authentication error:', userData.error);
        }
    } catch (error) {
        // Handle fetch or other errors
        console.error('Error fetching user information:', error);
        connectingElement.textContent = 'Error fetching user information. Please try again!';
        connectingElement.style.color = 'red';
    }
}

function onConnect() {

    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onNotificationReceived);

    //Subscribe to the user-specific topic
    stompClient.subscribe('/user/queue/notification', onNotificationReceived);

    // Subscribe to the project-specific topic
    stompClient.subscribe(`/topic/${projectId}/notification`, onNotificationReceived);

    // Subscribe to the project-specific topic
    stompClient.subscribe(`/topic/${projectId}/messages`, onMessageReceived);

    const chatMessage = {
        messageType: 'JOIN',
        content: '',
        user: {
            id: userId,
            username: username,
        },
        project: {
            id: projectId
        }
    };
    stompClient.send(`/app/${projectId}/connectUser`, {}, JSON.stringify(chatMessage));

    // connectingElement.classList.add('hidden');
    console.log("add user to connecting ")
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

const receivedPayloads = new Set();

async function onMessageReceived(payload) {
    const chatMessageList = JSON.parse(payload.body);

    console.log("This is chatmessage " + chatMessageList);

    chatMessageList.forEach((chatMessage) => {
        const stringifiedPayload = JSON.stringify(chatMessage);
        if (!receivedPayloads.has(stringifiedPayload)) {
            // If the payload is not a duplicate, process it
            toDisplayChatMessages(chatMessage);

            // Add the stringified payload to the set to mark it as received
            receivedPayloads.add(stringifiedPayload);
        } else {
            console.log("Duplicate payload. Skipping.");
        }
    });
}


function toDisplayChatMessages(chatMessage) {
    const listItem = document.createElement('li');

    if (chatMessage.messageType === 'JOIN') {
        listItem.classList.add('event-message');
        chatMessage.content = chatMessage.user.username + ' joined!';
    } else if (chatMessage.messageType === 'LEAVE') {
        listItem.classList.add('event-message');
        chatMessage.content = chatMessage.user.username + ' left!';
    } else {

        if (chatMessage.user.id !== userId) {
            listItem.classList.add('clearfix');
        } else {
            listItem.classList.add('clearfix', 'odd'); // Add 'odd' class here
        }

        // Create the chat-avatar div
        const chatAvatarDiv = document.createElement('div');
        chatAvatarDiv.classList.add('chat-avatar');

        // Create the image element inside chat-avatar div
        const avatarImage = document.createElement('img');
        avatarImage.src = '/images/users/avatar-5.jpg';
        avatarImage.classList.add('rounded');
        avatarImage.alt = chatMessage.user.name;

        // Create the i element inside chat-avatar div
        const timeIcon = document.createElement('i');
        timeIcon.textContent = chatMessage.timestamp;

        // Append image and i elements to the chat-avatar div
        chatAvatarDiv.appendChild(avatarImage);
        chatAvatarDiv.appendChild(timeIcon);

        // Create the conversation-text div
        const conversationTextDiv = document.createElement('div');
        conversationTextDiv.classList.add('conversation-text');

        // Create the ctext-wrap div inside conversation-text div
        const ctextWrapDiv = document.createElement('div');
        ctextWrapDiv.classList.add('ctext-wrap');

        // Create the i element inside ctext-wrap div
        const nameIcon = document.createElement('i');
        nameIcon.textContent = chatMessage.user.username;

        // Create the p element inside ctext-wrap div
        const messageParagraph = document.createElement('p');
        messageParagraph.textContent = chatMessage.content;

        // Append nameIcon and messageParagraph to ctext-wrap div
        ctextWrapDiv.appendChild(nameIcon);
        ctextWrapDiv.appendChild(messageParagraph);

        // Append ctextWrapDiv to conversation-text div
        conversationTextDiv.appendChild(ctextWrapDiv);

        // Append chatAvatarDiv and conversationTextDiv to the list item
        listItem.appendChild(chatAvatarDiv);
        listItem.appendChild(conversationTextDiv);

        // Now you can append the listItem to your existing conversation-list
        const conversationList = document.querySelector('.conversation-list');
        conversationList.appendChild(listItem);

        conversationList.scrollTop = conversationList.scrollHeight;
    }
}


async function onNotificationReceived(payload) {
    console.log("i am in notification received...")
    try {
        const notificationData = JSON.parse(payload.body);
        console.log('Parsed notification data:', notificationData);

        // Your processing logic here
        if (notificationData) {

            // showToast(notificationData.content, "", "success");

            // Create a new <a> element
            const newNotification = document.createElement('a');
            newNotification.href = "javascript:void(0);";
            newNotification.classList.add('dropdown-item', 'p-0', 'notify-item', 'unread-noti', 'card', 'm-0', 'shadow-none');

            // Construct the inner HTML of the <a> element using notificationData
            newNotification.innerHTML = `
                <div class="card-body">
                    <div class="d-flex align-items-center">
                        <div class="flex-shrink-0">
                            <div class="notify-icon bg-primary">
                                <i class="ri-message-3-line fs-18"></i>
                            </div>
                        </div>
                        <div class="flex-grow-1 text-truncate ms-2">
                            <h5 class="noti-item-title fw-medium fs-14">${notificationData.user?.username || 'Unknown User'} <small class="fw-normal text-muted float-end ms-1">${notificationData.time}</small></h5>
                            <small class="noti-item-subtitle text-muted">${notificationData.content}</small>
                        </div>
                    </div>
                </div>
            `;

            // Append the new <a> element to the notification container
            const notificationContainer = document.getElementById('notificationContainer');
            notificationContainer.appendChild(newNotification);
        }

    } catch (error) {
        console.error("Error parsing JSON:", error);
        console.log("Raw message content:", payload.body);
    }
}

function showToast(message, position, type) {
    const toast = document.getElementById("toast");
    console.log("in show Toast");
    toast.className = toast.className + " show";

    if (message) toast.innerText = message;

    if (position !== "") toast.className = toast.className + ` ${position}`;
    if (type !== "") toast.className = toast.className + ` ${type}`;

    setTimeout(function () {
        toast.className = toast.className.replace("show", "");
    }, 3000);
}

function getProjectIdFromUrl() {
    const currentUrl = window.location.href;
    const match = currentUrl.match(/\/homepage\/(\d+)/);
    if (match && match[1]) {
        return match[1];
    } else {
        return 1; //returing 1 is cheating
    }
}

//user level notification
function sendNotificationToSpecificUsers(userIds) {
    console.log("Users Id array: " + userIds);

    // if (stompClient) {
    //     const notificationRequest = {
    //         content: 'Hello, World!',
    //         projectId: projectId,
    //         userIds: userIds,
    //     };
    //     console.log("Notification Data: " + JSON.stringify(notificationRequest));
    //
    //     try {
    //         stompClient.send(`/app/specific`, {}, JSON.stringify(notificationRequest));
    //     } catch (error) {
    //         console.error('Error sending notification:', error);
    //     }
    // }
}


function sendNotificationToProject(project_id) {
    console.log("i am in sent notification to project level")
    if (stompClient) {
        const notificationData = {
            content: "something alerting to project",
            user: {
                id: userId,
            },
            project: {
                id: projectId,
            }
        };
        console.log("Notification Data is " + notificationData)
        stompClient.send(`/app/${projectId}/sendNotiToProject`, {}, JSON.stringify(notificationData));
    }
}

function sendPublicNoti() {
    const notification = {
        content: "public noti",
        user: {
            id: userId,
            username: username,
        },
        project: {
            id: projectId
        }
    }
    stompClient.send(`/app/public`, {}, JSON.stringify(notification));
}

document.addEventListener('DOMContentLoaded', async function () {
    await connect();
});


