'use strict';

const chatForm = document.getElementById('chat-form');
const connectingElement = document.querySelector('.connecting');
const messageInput = document.querySelector('#message');

let stompClient = null;
let username = null;
let userId = null;
let userEmail = null;
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
            userEmail = userData.email;

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

    //Subscribe to the user-specific topic
    stompClient.subscribe('/user/queue/notification', onNotificationReceived);

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

    connectingElement.textContent='';

    console.log("add user to connecting ")
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

let receivedPayloads = new Set();

async function onMessageReceived(payload) {
    const chatMessageList = JSON.parse(payload.body);

    console.log("This is chatmessage " + chatMessageList);

    chatMessageList.forEach((chatMessage) => {
        const stringifiedPayload = JSON.stringify(chatMessage);
        if (!receivedPayloads.has(stringifiedPayload)) {
            // If the payload is not a duplicate, process it
            displayChatMessages(chatMessage);

            // Add the stringified payload to the set to mark it as received
            receivedPayloads.add(stringifiedPayload);
        } else {
            console.log("Duplicate payload. Skipping.");
        }
    });
}

function displayChatMessages(chatMessage) {
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
    try {
        const notificationData = JSON.parse(payload.body);

        if (notificationData) {
            // Optionally, you can display the notification immediately
            displayNotification(notificationData);
        } else {
            console.log("Empty or invalid notification data received.");
        }
    } catch (error) {
        console.error("Error parsing JSON:", error);
    }
}

function displayNotification(notificationData) {
    const newNotification = createNotificationElement(notificationData);
    appendNotificationToContainer(newNotification);
}

function createNotificationElement(notificationData) {
    const newNotification = document.createElement('a');
    newNotification.href = getNotificationURL(notificationData);
    newNotification.classList.add('dropdown-item', 'p-0', 'notify-item', 'unread-noti', 'card', 'm-0', 'shadow-none');

    newNotification.innerHTML = constructNotificationHTML(notificationData);

    return newNotification;
}

function constructNotificationHTML(notificationData) {
    return `
        <div class="card mb-3">
            <div class="card-body">
                <div class="d-flex align-items-center">
                    <div class="flex-shrink-0">
                        <div class="notify-icon bg-primary">
                            <i class="ri-notification-3-fill fs-18 text-light"></i>
                        </div>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <div class="fw-bold">${notificationData.content}</div>
                        <small class="text-muted">
                            ${notificationData.timestamp}
                        </small>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function appendNotificationToContainer(notificationElement) {
    const notificationContainer = document.getElementById('notificationContainer');
    notificationContainer.appendChild(notificationElement);
}

// Function to display multiple notifications
function displayNotifications(notificationsList) {
    // Clear existing notifications before displaying new ones
    const notificationContainer = document.getElementById('notificationContainer');
    notificationContainer.innerHTML = '';

    // Display each notification
    notificationsList.forEach(notificationData => {
        displayNotification(notificationData);
    });
}


// Function to fetch notification data from the server
async function fetchNotifications(userId) {
    try {
        let url = `/api/notifications?userId=${userId}`;

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const notificationList = await response.json();
        console.log("noti liser " + notificationList);

        // Call the displayNotifications function to display the notifications
        displayNotifications(notificationList);

        return notificationList;
    } catch (error) {
        console.error('Error fetching notifications:', error);
        return [];
    }
}


let toControlFetchData = true; // Set to true initially to allow fetching data

document.addEventListener('DOMContentLoaded', async function () {
    await connect();

    if (toControlFetchData) {
        console.log("Am I called?");
        // Check if userId is not null before calling fetchNotifications
        if (userId) {
            try {
                // Call fetchNotifications and wait for it to complete
                const notifications = await fetchNotifications(userId);

                // Check if fetchNotifications was successful
                if (notifications) {
                    playAudio();

                    // Set toControlFetchData to false after successful fetch
                    toControlFetchData = false;
                } else {
                    console.error('Error fetching notifications. Check your fetchNotifications function.');
                }
            } catch (error) {
                console.error('Error fetching notifications:', error);
            }
        } else {
            console.error('User ID is null. Make sure to set it before calling fetchNotifications.');
        }
    }
});

function getProjectIdFromUrl() {
    const currentUrl = window.location.href;
    const match = currentUrl.match(/\/projectDetail\/(\d+)/);
    if (match && match[1]) {
        return match[1];
    } else {
        return 1; //returing 1 is cheating
    }
}

// Example function to construct the notification URL
function getNotificationURL(notificationData) {
    // Assuming notificationData has properties like projectLink and issueLink
    const projectLink = notificationData.projectId;
    const issueLink = notificationData.issueId;

    if (projectLink) {
        // If it's a project notification, construct the project URL
        return `/projectDetail/${projectLink}`; // Adjust this based on your project URL structure
    } else if (issueLink) {
        // If it's an issue notification, construct the issue URL
        return `/issues/${issueLink}`; // Adjust this based on your issue URL structure
    } else {
        // Default fallback URL (e.g., go to a generic notification page)
        return '/notifications'; // Adjust this based on your desired fallback URL
    }
}

function playAudio() {
    // Replace 'path/to/audio.mp3' with the actual path to your audio file
    const audioPath = 'audio/pop-noti.mp3';

    const audio = new Audio(audioPath);

    // Play the audio
    audio.play();
}



