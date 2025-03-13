document.getElementById("button-enter-acc").addEventListener("click", clickButtonEnter);

function clickButtonEnter() {
    const fullName = getFullName();
    if (!fullName) {
        showMessage("Введите хотя бы одну часть ФИО!", "error");
        return;
    }

    fetchBookings(fullName);
}

function getFullName() {
    const name = document.getElementById("input-name").value.trim();
    const surname = document.getElementById("input-surname").value.trim();
    const patronymic = document.getElementById("input-patronymic").value.trim();
    return [surname, name, patronymic].filter(Boolean).join(' ');
}

function fetchBookings(fullName) {
    const url = `http://localhost:8080/bookings/search?fullName=${encodeURIComponent(fullName)}`;

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error(`Ошибка: ${response.status}`);
            return response.json();
        })
        .then(bookings => {
            if (bookings.length === 0) {
                showMessage("Бронирования не найдены.", "info");
            } else {
                fetchRoutesForBookings(bookings);
            }
        })
        .catch(error => {
            showMessage("Ошибка при получении данных: " + error.message, "error");
        });
}

function fetchRoutesForBookings(bookings) {
    const routePromises = bookings.map(booking => 
        fetch(`http://localhost:8080/routes/${booking.routeid.id}`)
            .then(response => response.json())
            .then(route => {
                booking.route = route;
                return booking;
            })
            .catch(error => {
                console.error("Ошибка при получении информации о маршруте:", error);
                return booking;
            })
    );

    Promise.all(routePromises).then(bookingsWithRoutes => {
        displayBookings(bookingsWithRoutes);
    });
}

function displayBookings(bookings) {
    const resultContainer = document.getElementById("booking-results");
    resultContainer.innerHTML = "";

    if (bookings.length === 0) {
        showMessage("Бронирования не найдены.", "info");
    } else {
        resultContainer.style.display = "block";
        bookings.forEach(booking => {
            const card = createBookingCard(booking);
            resultContainer.appendChild(card);
        });
    }
}

function createBookingCard(booking) {
    const card = document.createElement("div");
    card.className = "card";

    const transportIcon = getTransportIcon(booking.routeid.transporttype);
    const timeInRoute = getTimeInRoute(new Date(booking.routeid.departuretime), new Date(booking.routeid.arrivaltime));

    card.innerHTML = `
        <div class="card-header">
            <img src="images/${transportIcon}" alt="${booking.routeid.transporttype}" class="transport-icon">
            <h3>${booking.routeid.transporttype}</h3>
        </div>
        <div class="points">
            <div class="departure-destination">
                <h2>${booking.routeid.departurepoint}</h2>
                <br>
                <h>Дата и время отправления: <br>${booking.routeid.departuretime}</h>
            </div>
            <div class="time-in-route-arrow">
                <h2 class="time-in-route">${timeInRoute}</h2>
                <svg class="arrow" width="197" height="30" viewBox="0 0 197 30" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M39.5 21.5L39.1467 23.4685L39.252 23.4874L39.3588 23.495L39.5 21.5ZM96 25.5L95.8588 27.495L96.0084 27.5056L96.1579 27.4938L96 25.5ZM146.5 21.5L146.658 23.4938L146.754 23.4862L146.849 23.4694L146.5 21.5ZM196.139 14.1458C196.772 13.2406 196.551 11.9936 195.646 11.3607L180.894 1.04722C179.988 0.414335 178.741 0.635148 178.108 1.54042C177.476 2.4457 177.696 3.69263 178.602 4.32552L191.715 13.4931L182.547 26.6063C181.914 27.5116 182.135 28.7585 183.04 29.3914C183.946 30.0243 185.193 29.8034 185.826 28.8982L196.139 14.1458ZM0.146653 16.4686L15.381 19.2029L16.0877 15.2658L0.853306 12.5315L0.146653 16.4686ZM23.9123 20.7342L39.1467 23.4685L39.8533 19.5315L24.6189 16.7971L23.9123 20.7342ZM39.3588 23.495L50.3939 24.2763L50.6764 20.2862L39.6412 19.505L39.3588 23.495ZM56.5736 24.7138L78.6439 26.2763L78.9264 22.2862L56.8561 20.7237L56.5736 24.7138ZM84.8236 26.7138L95.8588 27.495L96.1412 23.505L85.1061 22.7237L84.8236 26.7138ZM96.1579 27.4938L106.021 26.7125L105.705 22.725L95.8421 23.5062L96.1579 27.4938ZM111.545 26.275L131.271 24.7125L130.955 20.725L111.229 22.2875L111.545 26.275ZM136.795 24.275L146.658 23.4938L146.342 19.5062L136.479 20.2875L136.795 24.275ZM146.849 23.4694L156.224 21.8092L155.526 17.8705L146.151 19.5306L146.849 23.4694ZM161.474 20.8795L180.224 17.5591L179.526 13.6204L160.776 16.9408L161.474 20.8795ZM185.474 16.6294L194.849 14.9693L194.151 11.0305L184.776 12.6907L185.474 16.6294Z" fill="#44A1B1"/>
                </svg>
            </div>
            <div class="departure-destination">
                <h2>${booking.routeid.destinationpoint}</h2>
                <br>
                <h>Дата и время прибытия: <br>${booking.routeid.arrivaltime}</h>
            </div>
        </div>
        <div class="card-body">
            <h><strong>Пассажир:</strong> ${booking.passengername}</h>
            <h><strong>Дата бронирования:</strong> ${booking.bookingdate}</h>
            <button class="button" id="button-cancel-${booking.idbooking}">Отменить бронь</button>
        </div>
    `;

    const cancelButton = card.querySelector(`#button-cancel-${booking.idbooking}`);
    cancelButton.addEventListener("click", () => {
        if (confirm("Вы уверены, что хотите отменить бронирование?")) {
            deleteBooking(booking.idbooking, card);
        }
    });

    return card;
}

function getTransportIcon(transportType) {
    if (transportType.toLowerCase().includes('самолет')) return 'plane.png';
    if (transportType.toLowerCase().includes('поезд')) return 'train.png';
    if (transportType.toLowerCase().includes('автобус')) return 'bus.png';
    return '';
}

function deleteBooking(bookingId, cardElement) {
    fetch(`http://localhost:8080/bookings/${bookingId}`, {
        method: "DELETE"
    })
    .then(response => {
        if (response.ok) {
            cardElement.remove();
            showMessage("Бронирование успешно отменено.", "info");
        } else {
            throw new Error("Ошибка при отмене бронирования.");
        }
    })
    .catch(error => {
        console.error("Ошибка:", error);
        showMessage("Не удалось отменить бронирование.", "error");
    });
}

function getTimeInRoute(departureTime, arrivalTime) {
    const timeDifference = arrivalTime - departureTime;
    const hours = Math.floor(timeDifference / (1000 * 60 * 60));
    const minutes = Math.floor((timeDifference % (1000 * 60 * 60)) / (1000 * 60));
    return `${hours} ч ${minutes} мин`;
}

function showMessage(message, type) {
    const resultContainer = document.getElementById("booking-results");
    resultContainer.innerHTML = "";

    const messageDiv = document.createElement("div");
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = message;

    resultContainer.appendChild(messageDiv);
}