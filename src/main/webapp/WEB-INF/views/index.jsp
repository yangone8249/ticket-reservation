<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>티켓 예약</title>
<style>
  body { font-family: sans-serif; max-width: 600px; margin: 60px auto; }
  h1, h2 { font-size: 20px; }
  input, button { font-size: 16px; padding: 8px; margin-top: 8px; width: 100%; box-sizing: border-box; }
  button { cursor: pointer; }
  #result { margin-top: 16px; white-space: pre-wrap; }

  #seatSection { display: none; margin-top: 24px; border-top: 1px solid #ddd; padding-top: 16px; }
  .seat-grid { display: flex; flex-direction: column; gap: 8px; margin-top: 16px; }
  .seat-row { display: flex; gap: 6px; align-items: center; }
  .seat-row-label { width: 20px; font-weight: bold; }
  .seat {
    width: 40px; height: 40px;
    border: 1px solid #999; border-radius: 4px;
    background: #fff; cursor: pointer; font-size: 12px;
    margin-top: 0;
  }
  .seat.AVAILABLE { background: #e6f7e6; border-color: #4caf50; }
  .seat.RESERVED { background: #f0f0f0; border-color: #ccc; color: #aaa; cursor: not-allowed; }
  .seat.selected { background: #4caf50; color: white; }
</style>
</head>
<body>
  <h1>치열한 예매 테스트</h1>
<!--  <input id="seatId" type="number" value="1" placeholder="좌석 ID">
  <input id="reserverName" type="text" placeholder="예약자 이름">-->
  <button id="reserveBtn">예약하기</button>
  <button id="viewSeatsBtn">현재 좌석 보기</button>
  <div id="result"></div>

  <div id="seatSection">
    <select id="concertSelect"></select>
    <div id="seatGrid" class="seat-grid">로딩 중...</div>
  </div>

<script>
let selectedConcertId = null;
let selectedSeatId = null;

document.getElementById('reserveBtn').addEventListener('click', async () => {
  const seatId = document.getElementById('seatId').value;
  const reserverName = document.getElementById('reserverName').value;
  await doReserve(seatId, reserverName);
});

document.getElementById('viewSeatsBtn').addEventListener('click', () => {
  const section = document.getElementById('seatSection');
  const hidden = section.style.display === 'none' || !section.style.display;
  section.style.display = hidden ? 'block' : 'none';
  if (hidden) loadConcerts();
});

document.getElementById('concertSelect').addEventListener('change', (e) => {
  selectedConcertId = e.target.value;
  loadSeats();
});

async function loadConcerts() {
  const select = document.getElementById('concertSelect');
  try {
    const res = await fetch('/api/concerts');
    const concerts = await res.json();
    select.innerHTML = concerts
      .map(c => `<option value="${c.id}">${c.title}</option>`)
      .join('');
    if (concerts.length > 0) {
      selectedConcertId = concerts[0].id;
      loadSeats();
    }
  } catch (e) {
    document.getElementById('seatGrid').textContent = '콘서트 목록을 불러오지 못했습니다: ' + e.message;
  }
}

async function doReserve(seatId, reserverName) {
  const result = document.getElementById('result');
  result.textContent = '요청 중...';
  try {
    const res = await fetch(`/api/seats/${seatId}/reservations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ reserverName })
    });
    if (res.ok) {
      const data = await res.json();
      result.textContent = `예약 성공! reservationId=${data.reservationId}`;
      loadSeats();
    } else {
      const text = await res.text();
      result.textContent = `예약 실패: ${text}`;
    }
  } catch (e) {
    result.textContent = `에러: ${e.message}`;
  }
}

async function loadSeats() {
  if (!selectedConcertId) return;
  const grid = document.getElementById('seatGrid');
  try {
    const res = await fetch(`/api/concerts/${selectedConcertId}/seats`);
    if (!res.ok) {
      grid.textContent = '좌석 정보를 불러오지 못했습니다. (' + res.status + ')';
      return;
    }
    const seats = await res.json();
    renderSeats(seats);
  } catch (e) {
    grid.textContent = '에러: ' + e.message;
  }
}

function renderSeats(seats) {
  const grid = document.getElementById('seatGrid');
  grid.innerHTML = '';

  const rows = {};
  seats.forEach(seat => {
    const rowLabel = seat.seatNumber.charAt(0);
    if (!rows[rowLabel]) rows[rowLabel] = [];
    rows[rowLabel].push(seat);
  });

  Object.keys(rows).sort().forEach(rowLabel => {
    const rowDiv = document.createElement('div');
    rowDiv.className = 'seat-row';

    const label = document.createElement('div');
    label.className = 'seat-row-label';
    label.textContent = rowLabel;
    rowDiv.appendChild(label);

    rows[rowLabel]
      .sort((a, b) => parseInt(a.seatNumber.slice(1)) - parseInt(b.seatNumber.slice(1)))
      .forEach(seat => {
        const btn = document.createElement('button');
        btn.className = `seat ${seat.status}`;
        btn.textContent = seat.seatNumber;
        btn.disabled = seat.status !== 'AVAILABLE';
        btn.addEventListener('click', () => selectSeat(seat.id, btn));
        rowDiv.appendChild(btn);
      });

    grid.appendChild(rowDiv);
  });
}

function selectSeat(seatId, btn) {
  document.querySelectorAll('.seat.selected').forEach(el => el.classList.remove('selected'));
  btn.classList.add('selected');
  selectedSeatId = seatId;

  setTimeout(() => {
    const reserverName = prompt('예약자 이름을 입력하세요');
    if (!reserverName) return;
    doReserve(selectedSeatId, reserverName);
  }, 0);
}
</script>
</body>
</html>
