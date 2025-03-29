select * From transaction

select * from account

select* from message

ALTER TABLE eventuate.message ADD COLUMN IF NOT EXISTS message_partition VARCHAR(1000);
