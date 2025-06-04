BEGIN
    CREATE DATABASE MovieTicketSystemDB;
END;
GO

-- Sử dụng cơ sở dữ liệu vừa tạo
-- Use the newly created database
SELECT * FROM Seats
USE MovieTicketSystemDB;
GO

-- Bảng Movies: Lưu trữ thông tin về các bộ phim
-- Movies Table: Stores information about movies
IF OBJECT_ID('Movies', 'U') IS NOT NULL
SELECT * FROM SEATS
SELECT * FROM MOVIES
SELECT * FROM ShowTimes
DROP TABLE Movies;
CREATE TABLE Movies (
    MovieID INT PRIMARY KEY IDENTITY(1,1), -- ID phim tự động tăng
    Title NVARCHAR(255) NOT NULL,          -- Tên phim
    Genre NVARCHAR(100),                   -- Thể loại
    DurationMinutes INT,                   -- Thời lượng phim (phút)
    Price DECIMAL(10, 2) NOT NULL          -- Giá vé mặc định cho phim này
);
GO

-- Bảng ShowTimes: Lưu trữ thông tin về các suất chiếu
-- ShowTimes Table: Stores information about showtimes
IF OBJECT_ID('ShowTimes', 'U') IS NOT NULL
DROP TABLE ShowTimes;
CREATE TABLE ShowTimes (
    ShowTimeID INT PRIMARY KEY IDENTITY(1,1), -- ID suất chiếu tự động tăng
    MovieID INT NOT NULL,                     -- Khóa ngoại liên kết với bảng Movies
    ShowTimeDateTime DATETIME NOT NULL,       -- Thời gian chiếu
    TheaterNumber INT NOT NULL,               -- Số rạp
    TotalSeats INT NOT NULL,                  -- Tổng số ghế trong rạp cho suất chiếu này
    AvailableSeats INT NOT NULL,              -- Số ghế còn trống
    CONSTRAINT FK_ShowTime_Movie FOREIGN KEY (MovieID) REFERENCES Movies(MovieID)
);
GO

-- Bảng Seats: Lưu trữ thông tin chi tiết về từng ghế trong một suất chiếu
-- Trạng thái ghế sẽ được quản lý trong ứng dụng Java để xử lý đồng thời
-- Seats Table: Stores detailed information about each seat in a showtime
-- Seat status will be managed in the Java application for concurrency handling
IF OBJECT_ID('Seats', 'U') IS NOT NULL
DROP TABLE Seats;
CREATE TABLE Seats (
    SeatID INT PRIMARY KEY IDENTITY(1,1),     -- ID ghế tự động tăng
    ShowTimeID INT NOT NULL,                  -- Khóa ngoại liên kết với bảng ShowTimes
    SeatRow NVARCHAR(10) NOT NULL,            -- Hàng ghế (ví dụ: 'A', 'B', 'C')
    SeatNumber INT NOT NULL,                  -- Số ghế trong hàng (ví dụ: 1, 2, 3)
    -- Trạng thái ghế (0: Trống, 1: Đã bán, 2: Đã hủy) - Có thể được quản lý chủ yếu trong Java với AtomicBoolean/Integer
    -- SeatStatus INT NOT NULL DEFAULT 0,
    CONSTRAINT UQ_ShowTime_Seat UNIQUE (ShowTimeID, SeatRow, SeatNumber), -- Đảm bảo mỗi ghế là duy nhất trong một suất chiếu
    CONSTRAINT FK_Seat_ShowTime FOREIGN KEY (ShowTimeID) REFERENCES ShowTimes(ShowTimeID)
);
GO

-- Bảng Tickets: Lưu trữ thông tin về các vé đã đặt
-- Tickets Table: Stores information about booked tickets
IF OBJECT_ID('Tickets', 'U') IS NOT NULL
DROP TABLE Tickets;
CREATE TABLE Tickets (
    TicketID INT PRIMARY KEY IDENTITY(1,1),   -- ID vé tự động tăng
    ShowTimeID INT NOT NULL,                  -- Khóa ngoại liên kết với bảng ShowTimes
    SeatID INT NOT NULL,                      -- Khóa ngoại liên kết với bảng Seats
    BookingTime DATETIME DEFAULT GETDATE(),   -- Thời gian đặt vé
    CustomerName NVARCHAR(255),               -- Tên khách hàng (hoặc ID khách hàng nếu có bảng Users)
    Price DECIMAL(10, 2) NOT NULL,            -- Giá vé tại thời điểm đặt
    IsCanceled BIT DEFAULT 0,                 -- Trạng thái hủy vé (0: Chưa hủy, 1: Đã hủy)
    CONSTRAINT UQ_Ticket_Seat_ShowTime UNIQUE (ShowTimeID, SeatID), -- Đảm bảo một ghế chỉ có một vé cho một suất chiếu
    CONSTRAINT FK_Ticket_ShowTime FOREIGN KEY (ShowTimeID) REFERENCES ShowTimes(ShowTimeID),
    CONSTRAINT FK_Ticket_Seat FOREIGN KEY (SeatID) REFERENCES Seats(SeatID)
);
GO

-- Thêm một số dữ liệu mẫu (Tùy chọn)
-- Insert some sample data (Optional)

-- Chèn dữ liệu mẫu vào bảng Movies
-- Insert sample data into Movies table
INSERT INTO Movies (Title, Genre, DurationMinutes, Price) VALUES
(N'Kẻ Cắp Mặt Trăng 4', N'Hoạt hình, Hài', 95, 120000.00),
(N'Lật Mặt 7: Một Điều Ước', N'Hài, Tâm lý', 130, 150000.00),
(N'Dune: Part Two', N'Khoa học viễn tưởng, Phiêu lưu', 166, 140000.00);
GO

-- Chèn dữ liệu mẫu vào bảng ShowTimes
-- Insert sample data into ShowTimes table
INSERT INTO ShowTimes (MovieID, ShowTimeDateTime, TheaterNumber, TotalSeats, AvailableSeats) VALUES
(1, '2025-06-04 10:00:00', 1, 100, 100),
(1, '2025-06-04 14:00:00', 1, 100, 100),
(2, '2025-06-04 12:30:00', 2, 80, 80),
(3, '2025-06-04 18:00:00', 3, 120, 120);
GO

-- Chèn dữ liệu mẫu cho ghế của suất chiếu đầu tiên (MovieID 1, ShowTimeID 1)
-- Giả sử rạp 1 có 10 hàng (A-J) và 10 cột (1-10)
-- Insert sample data for seats of the first showtime (MovieID 1, ShowTimeID 1)
-- Assuming Theater 1 has 10 rows (A-J) and 10 columns (1-10)
DECLARE @ShowTimeID1 INT = (SELECT ShowTimeID FROM ShowTimes WHERE MovieID = 1 AND ShowTimeDateTime = '2025-06-04 10:00:00');
DECLARE @rowChar CHAR(1);
DECLARE @seatNum INT;

SET @rowChar = 'A';
WHILE @rowChar <= 'J'
BEGIN
    SET @seatNum = 1;
    WHILE @seatNum <= 10
    BEGIN
        INSERT INTO Seats (ShowTimeID, SeatRow, SeatNumber) VALUES (@ShowTimeID1, @rowChar, @seatNum);
        SET @seatNum = @seatNum + 1;
    END;
    SET @rowChar = CHAR(ASCII(@rowChar) + 1);
END;
GO

-- Chèn dữ liệu mẫu cho ghế của suất chiếu thứ hai (MovieID 2, ShowTimeID 3)
-- Giả sử rạp 2 có 8 hàng (A-H) và 10 cột (1-10)
-- Insert sample data for seats of the second showtime (MovieID 2, ShowTimeID 3)
-- Assuming Theater 2 has 8 rows (A-H) and 10 columns (1-10)
DECLARE @ShowTimeID2 INT = (SELECT ShowTimeID FROM ShowTimes WHERE MovieID = 2 AND ShowTimeDateTime = '2025-06-04 12:30:00');
DECLARE @rowChar2 CHAR(1);
DECLARE @seatNum2 INT;

SET @rowChar2 = 'A';
WHILE @rowChar2 <= 'H'
BEGIN
    SET @seatNum2 = 1;
    WHILE @seatNum2 <= 10
    BEGIN
        INSERT INTO Seats (ShowTimeID, SeatRow, SeatNumber) VALUES (@ShowTimeID2, @rowChar2, @seatNum2);
        SET @seatNum2 = @seatNum2 + 1;
    END;
    SET @rowChar2 = CHAR(ASCII(@rowChar2) + 1);
END;
GO

SELECT * FROM ShowTimesSQL Query to create database:


In the src/main/resources/application.properties, change the datasource.url to your local data source
