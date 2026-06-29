<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>티켓 예약</title>
<style>
  body { font-family: sans-serif; max-width: 480px; margin: 60px auto; }
  h1 { font-size: 20px; }
  input, button { font-size: 16px; padding: 8px; margin-top: 8px; width: 100%; box-sizing: border-box; }
  button { cursor: pointer; }
  #result { margin-top: 16px; white-space: pre-wrap; }
</style>
</head>
<body>
  <h1>테스트 콘서트 - A1석 예약</h1>
  <input id="seatId" type="number" value="1" placeholder="좌석 ID">
  <input id="reserverName" type="text" placeholder="예약자 이름">
  <button id="reserveBtn">예약하기</button>
  <div id="result"></div>

<script>
document.getElementById('reserveBtn').addEventListener('click', async () => {
  const seatId = document.getElementById('seatId').value;
  const reserverName = document.getElementById('reserverName').value;
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
    } else {
      const text = await res.text();
      result.textContent = `예약 실패: ${text}`;
    }
  } catch (e) {
    result.textContent = `에러: ${e.message}`;
  }
});
</script>
</body>
</html>
