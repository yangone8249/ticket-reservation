docker exec ticket-reservation-mysql mysql -uroot -proot ticket_reservation -e "DELETE FROM reservation; UPDATE seat SET status='AVAILABLE', version=0;"
docker exec ticket-reservation-redis redis-cli FLUSHALL
Write-Output "리셋 완료: seat AVAILABLE, version=0, reservation 비움, Redis 락 비움"
