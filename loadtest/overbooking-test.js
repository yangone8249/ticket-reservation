import http from 'k6/http';

export const options = {
    scenarios: {
        overbooking: {
            executor: 'shared-iterations',
            vus: 100,
            iterations: 100,
            maxDuration: '30s',
        },
    },
};

export default function () {
    const url = 'http://localhost:8080/api/seats/1/reservations';
    const payload = JSON.stringify({ reserverName: `user-${__VU}-${__ITER}` });
    const params = { headers: { 'Content-Type': 'application/json' } };

    http.post(url, payload, params);
}
