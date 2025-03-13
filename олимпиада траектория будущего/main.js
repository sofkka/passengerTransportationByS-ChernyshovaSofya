fetch('http://localhost:8080/routes')
    .then(response => response.json())
    .then(data => {
        console.log("Данные маршрутов:", data); // Логируем данные маршрутов
        if (data && Array.isArray(data)) {
            displayRoutes(data);
            displayDeparturePoint(data);
            displayTransports(data);
            displayArrivalPoint(data);
        } else {
            console.error("Ошибка: данные маршрутов не получены или не являются массивом");
        }
    })
    .catch(error => console.error("Ошибка при запросе:", error));


  function displayTransports(routes) {
    if (!routes || !Array.isArray(routes)) {
        console.error("Ошибка: данные маршрутов не переданы или не являются массивом");
        return;
    }

    const transportList = document.getElementById('transport-type');
    while (transportList.options.length > 1) {
        transportList.remove(1);
    }

    const uniqueTransportTypes = new Set();
    routes.forEach(route => {
        uniqueTransportTypes.add(route.transporttype);
    });

    const mixOption = document.createElement('option');
    mixOption.value = 'Микс';
    mixOption.textContent = 'Микс';
    transportList.appendChild(mixOption);

    uniqueTransportTypes.forEach(transportType => {
        const option = document.createElement('option');
        option.value = transportType;
        option.textContent = transportType;
        transportList.appendChild(option);
    });
}

// Функция для отображения возможных пунктов отправления
function displayDeparturePoint(routes) {
  const departurePointList = document.getElementById('departure'); // Находим select для пунктов отправления
  // Не удаляем первую опцию (подсказку)
  while (departurePointList.options.length > 1) {
    departurePointList.remove(1); // Удаляем все опции, кроме первой
  }

  // Создаем Set для хранения уникальных пунктов отправления
  const uniqueDeparturePoint = new Set();

  // Перебираем маршруты и добавляем пункты отправления в Set
  routes.forEach(route => {
    uniqueDeparturePoint.add(route.departurepoint);
  });

  // Преобразуем Set в массив и сортируем по алфавиту
  const sortedDeparturePoints = Array.from(uniqueDeparturePoint).sort();

  // Добавляем каждый уникальный пункт отправления в select
  sortedDeparturePoints.forEach(departurepoint => {
    const option = document.createElement('option');
    option.value = departurepoint; // Устанавливаем значение option
    option.textContent = departurepoint; // Устанавливаем текст опции
    departurePointList.appendChild(option); // Добавляем option в select
  });
}

// Функция для отображения возможных пунктов прибытия
function displayArrivalPoint(routes) {
  const arrivalPointList = document.getElementById('arrival'); // Находим select для пунктов прибытия
  // Не удаляем первую опцию (подсказку)
  while (arrivalPointList.options.length > 1) {
    arrivalPointList.remove(1); // Удаляем все опции, кроме первой
  }

  // Создаем Set для хранения уникальных пунктов прибытия
  const uniqueArrivalPoint = new Set();

  // Перебираем маршруты и добавляем пункты прибытия в Set
  routes.forEach(route => {
    uniqueArrivalPoint.add(route.destinationpoint);
  });

  // Преобразуем Set в массив и сортируем по алфавиту
  const sortedArrivalPoints = Array.from(uniqueArrivalPoint).sort();

  // Добавляем каждый уникальный пункт прибытия в select
  sortedArrivalPoints.forEach(destinationpoint => {
    const option = document.createElement('option');
    option.value = destinationpoint; // Устанавливаем значение option
    option.textContent = destinationpoint; // Устанавливаем текст опции
    arrivalPointList.appendChild(option); // Добавляем option в select
  });
}


// Функция нахождения времени в пути
function getTimeInRoute(departuretime, arrivaltime) {
  // Вычисляем разницу во времени в миллисекундах
  const timeDifference = arrivaltime - departuretime;

  // Преобразуем разницу в часы и минуты
  const hours = Math.floor(timeDifference / (1000 * 60 * 60)); // Часы
  const minutes = Math.floor((timeDifference % (1000 * 60 * 60)) / (1000 * 60)); // Минуты

  // Возвращаем время в формате "X ч Y мин"
  return `${hours} ч ${minutes} мин`;
}

// Функция для обработки изменения интервала дат
document.getElementById("checkbox-departure-date-interval").addEventListener("click", function () {
  changeDepartureDateInterval();
});

function changeDepartureDateInterval() {
  if (document.getElementById("checkbox-departure-date-interval").checked) {
      document.getElementById("departure-date").style.display = 'none';
      document.getElementById("departure-date").value = null;
      document.getElementById("departure-date-interval").style.display = 'grid';
  } else {
      document.getElementById("departure-date").style.display = 'block';
      document.getElementById("departure-date-interval").style.display = 'none';
      document.getElementById("departure-date-start").value = null;
      document.getElementById("departure-date-end").value = null;
  }
}

// Функция для поиска маршрутов по фильтрам
document.getElementById("button-search").addEventListener("click", function () {
  clickButtonSearch();
});


function clickButtonSearch() {
  // Получение элементов формы
  const departure = document.getElementById("departure");
  const arrival = document.getElementById("arrival");
  const transportType = document.getElementById("transport-type");
  const departureDate = document.getElementById("departure-date");
  const departureDateStart = document.getElementById("departure-date-start");
  const departureDateEnd = document.getElementById("departure-date-end");

  // Проверка на существование элементов формы
  if (!departure || !arrival || !transportType) {
    window.alert("Один или несколько элементов формы не найдены!");
    return;
  }

  // Проверка на заполненность обязательных полей
  if (!departure.value || !arrival.value || !transportType.value) {
    window.alert("Пожалуйста, заполните все обязательные поля!");
    return;
  }

  if (!departureDate.value && !departureDateStart.value && !departureDateEnd.value) {
    window.alert("Пожалуйста, укажите дату или интервал дат!");
    return;
  }

  if (departure.value === arrival.value) {
    window.alert("Вы не можете указать в пункте отправления и прибытия один и тот же город!");
    return;
  }

  // Сбор параметров для запроса
  const params = new URLSearchParams({
    departurepoint: departure.value,
    destinationpoint: arrival.value,
    transporttype: transportType.value === "Микс" ? "" : transportType.value,
    startDate: departureDate.value || "",
    dateOne: departureDateStart.value || "",
    dateTwo: departureDateEnd.value || "",
  });

  console.log("Параметры запроса:", params.toString()); // Логируем параметры

  // Отправка запроса на сервер
  fetch(`http://localhost:8080/filteredRoutes?${params.toString()}`)
    .then(response => {
      console.log("Ответ сервера:", response); // Логируем ответ
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('Ошибка при получении данных с сервера');
      }
    })
    .then(data => {
      console.log("Отфильтрованные данные:", data); // Логируем данные
      if (data.length === 0) {
        // Если маршрутов нет, выводим сообщение
        document.querySelector('.routes-list').innerHTML = `
          <div class="no-routes-message">
            <p>К сожалению, по вашему запросу не нашлось подходящих маршрутов. 😔</p>
            <p>Попробуйте изменить параметры поиска.</p>
          </div>
        `;
      } else {
        displayRoutes(data); // Отображаем маршруты
      }
    })
    .catch(error => {
      console.error("Ошибка при фильтрации:", error); // Логируем ошибку
      // Выводим сообщение об ошибке в блоке с маршрутами
      document.querySelector('.routes-list').innerHTML = `
        <div class="error-message">
          <p>К сожалению, маршрутов с такими параметрами нет :( </p>
        </div>
      `;
    });
}


// Функция для отображения маршрутов
function displayRoutes(routes) {
  const routesList = document.querySelector('.routes-list');
  routesList.innerHTML = '';

  routes.forEach(route => {
      const routeContainer = document.createElement('div');
      routeContainer.className = 'route-container';

      // Определяем изображение по типу транспорта
      let transportIcon = '';
      if (route.transporttype.toLowerCase().includes('самолет')) {
          transportIcon = 'plane.png';
      } else if (route.transporttype.toLowerCase().includes('поезд')) {
          transportIcon = 'train.png';
      } else if (route.transporttype.toLowerCase().includes('автобус')) {
          transportIcon = 'bus.png';
      }

      // Преобразуем строки времени в объекты Date
      const departureTime = new Date(route.departuretime);
      const arrivalTime = new Date(route.arrivaltime);
      const timeInRoute = getTimeInRoute(departureTime, arrivalTime);

      // Устанавливаем HTML-разметку
      routeContainer.innerHTML = `
          <div class="card" data-route-id="${route.idRoute}">
              <div class="card-header">
                  <img src="images/${transportIcon}" alt="${route.transporttype}" class="transport-icon">
                  <h3>${route.transporttype}</h3>
              </div>

              <div class="points">
                  <div class="departure-destination">
                      <h2>${route.departurepoint}</h2>
                      <br>
                      <h>Дата и время отправления: <br>${route.departuretime}</h>
                  </div>
                  
                  <div class="time-in-route-arrow">
                      <h2 class="time-in-route">${timeInRoute}</h2>
                      <svg class="arrow" width="197" height="30" viewBox="0 0 197 30" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <path d="M39.5 21.5L39.1467 23.4685L39.252 23.4874L39.3588 23.495L39.5 21.5ZM96 25.5L95.8588 27.495L96.0084 27.5056L96.1579 27.4938L96 25.5ZM146.5 21.5L146.658 23.4938L146.754 23.4862L146.849 23.4694L146.5 21.5ZM196.139 14.1458C196.772 13.2406 196.551 11.9936 195.646 11.3607L180.894 1.04722C179.988 0.414335 178.741 0.635148 178.108 1.54042C177.476 2.4457 177.696 3.69263 178.602 4.32552L191.715 13.4931L182.547 26.6063C181.914 27.5116 182.135 28.7585 183.04 29.3914C183.946 30.0243 185.193 29.8034 185.826 28.8982L196.139 14.1458ZM0.146653 16.4686L15.381 19.2029L16.0877 15.2658L0.853306 12.5315L0.146653 16.4686ZM23.9123 20.7342L39.1467 23.4685L39.8533 19.5315L24.6189 16.7971L23.9123 20.7342ZM39.3588 23.495L50.3939 24.2763L50.6764 20.2862L39.6412 19.505L39.3588 23.495ZM56.5736 24.7138L78.6439 26.2763L78.9264 22.2862L56.8561 20.7237L56.5736 24.7138ZM84.8236 26.7138L95.8588 27.495L96.1412 23.505L85.1061 22.7237L84.8236 26.7138ZM96.1579 27.4938L106.021 26.7125L105.705 22.725L95.8421 23.5062L96.1579 27.4938ZM111.545 26.275L131.271 24.7125L130.955 20.725L111.229 22.2875L111.545 26.275ZM136.795 24.275L146.658 23.4938L146.342 19.5062L136.479 20.2875L136.795 24.275ZM146.849 23.4694L156.224 21.8092L155.526 17.8705L146.151 19.5306L146.849 23.4694ZM161.474 20.8795L180.224 17.5591L179.526 13.6204L160.776 16.9408L161.474 20.8795ZM185.474 16.6294L194.849 14.9693L194.151 11.0305L184.776 12.6907L185.474 16.6294Z" fill="#44A1B1"/>
                      </svg>
                  </div>
                  
                  <div class="departure-destination">
                      <h2>${route.destinationpoint}</h2>
                      <br>
                      <h>Дата и время прибытия: <br>${route.arrivaltime}</h>
                  </div>
              </div>
              <div class="card-body">
                  <h><strong>Количество доступных мест:</strong> ${route.availableseats}</h>
                  <button class="button button-book">Забронировать</button>
              </div>
          </div>
      `;

      routesList.appendChild(routeContainer);
  });
}


// Обработка клика по кнопке "Забронировать"
document.addEventListener("click", (event) => {
  if (event.target.classList.contains("button-book")) {
    const routeCard = event.target.closest(".card");
    const idRoute = Number(routeCard.dataset.routeId);

    const routeDetails = {
      idRoute: idRoute,
      departure: routeCard.querySelector(".departure-destination h2").textContent,
      destination: routeCard.querySelector(".departure-destination:last-child h2").textContent,
      transport: routeCard.querySelector(".card-header h3").textContent,
      departureTime: routeCard.querySelector(".departure-destination h").textContent,
      arrivalTime: routeCard.querySelector(".departure-destination:last-child h").textContent,
    };

    document.getElementById("booking-modal").dataset.routeDetails = JSON.stringify(routeDetails);
    document.getElementById("booking-modal").style.display = "block";
  }
});

// Обработка подтверждения бронирования
document.getElementById("confirm-booking").addEventListener("click", () => {
  const routeDetails = JSON.parse(document.getElementById("booking-modal").dataset.routeDetails);

  const name = document.getElementById("input-name").value;
  const surname = document.getElementById("input-surname").value;
  const patronymic = document.getElementById("input-patronymic").value;

  if (!name || !surname || !patronymic) {
    window.alert("Пожалуйста, заполните все поля ФИО!");
    return;
  }

  const fullName = `${surname} ${name} ${patronymic}`;

  if (routeDetails && routeDetails.idRoute) {
    const idRoute = Number(routeDetails.idRoute);

    const url = new URL("http://localhost:8080/bookings/createNewBooking");
    url.searchParams.append("routeId", idRoute);
    url.searchParams.append("passengerName", fullName);
    url.searchParams.append("bookingDate", formatDateForServer(new Date()));


    fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then(response => {
        if (!response.ok) {
          throw new Error(`Ошибка сервера: ${response.status} ${response.statusText}`);
        }
        return response.json();
      })
      .then(data => {
        console.log("Бронирование создано:", data);
        document.getElementById("booking-modal").style.display = "none";
        window.alert("Вы зарегистрировались. Теперь можете просматривать свои бронирования в личном кабинете.");
        window.location.reload(); // Перезагружаем страницу для обновления данных
      })
      .catch(error => {
        console.error("Ошибка при создании бронирования:", error.message || error);
        window.alert("Произошла ошибка при бронировании. Пожалуйста, попробуйте еще раз.");
      });
  } else {
    console.error("Ошибка: routeDetails не содержит idRoute.");
  }
});

// Функция для преобразования даты в формат "YYYY-MM-DD HH:MM"
function formatDateForServer(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
}



// Получаем модальное окно
    var modal = document.getElementById('booking-modal');

    // Получаем элемент <span>, который закрывает модальное окно
    var span = document.getElementsByClassName('close')[0];

    // Когда пользователь нажимает на <span> (x), закрываем модальное окно
    span.onclick = function() {
        modal.style.display = 'none';
    }

    // Когда пользователь кликает вне модального окна, оно также закрывается
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }
