
# [ProTuber]

[Mô tả dự án]

ProTuber giúp bạn tải các link video từ trên mạng về máy với độ phân giải, chất lượng audio ở mức tốt nhất. Ngoài ra bạn có thể quản lý các video đã tải ngay trong app như thêm sửa, xóa, viết note, tag, timestamp

## Authors

- [@longtrandev2](https://github.com/longtrandev2)


## Demo

https://youtu.be/-sLkMPXCdfg

## Screenshots
![alt text](<Demo-Images/Screenshot 2025-03-25 102132.png>)

![alt text](<Demo-Images/Screenshot 2025-03-25 102258.png>)

![alt text](<Demo-Images/Screenshot 2025-03-25 102306.png>)
## Features

- Tải các video trên mạng/ở máy về thêm vào app ở chất lượng tốt nhất có thể
- Quản lý các video thêm, sửa, xóa.
- Có thêm note, tag, timestamp cho các video

**Lưu ý**: Nếu ấn vào phát video chưa thấy video hiện thì quay lại trang chủ ấn lại nhé (Có thể do video chưa load xong).
##  File Structure

ProTuber/
│──ffmpeg
│──yt-dlp.exe
│── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── org.example.protuber/
│   │   │   │   ├── controller/
│   │   │   │   │   ├── FontController.java
│   │   │   │   │   ├── HomeViewController.java
│   │   │   │   │   ├── MainViewController.java
│   │   │   │   │   ├── NoteController.java
│   │   │   │   │   ├── VideoItemController.java
│   │   │   │   │   ├── VideoPlayerController.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Timestamp.java
│   │   │   │   │   ├── Video.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── VideoService.java
│   │   │   │   ├── storage/
│   │   │   │   │   ├── VideoStorage.java
│   │   │   │   ├── utils/
│   │   │   │   │   ├── MediaValidator.java
│   │   │   │   │   ├── YTDLHelper.java
│   │   │   │   ├── Main.java
│   │   │   │   ├── module-info.java
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   ├── org.example.protuber/
│   │   │   │   ├── fonts/
│   │   │   │   ├── icons/
│   │   │   │   ├── styles/
│   │   │   │   ├── view/
│   │   │   │   │   ├── HomeView.fxml
│   │   │   │   │   ├── MainView.fxml
│   │   │   │   │   ├── NoteView.fxml
│   │   │   │   │   ├── VideoItemView.fxml
│   │   │   │   │   ├── VideoPlayerView.fxml

## Requirements

Java: Version 17 or higher (this project uses Java 23).
Maven: Version 3.6.3 or higher (for building the project from source).
Operating System: Windows, macOS, or Linux (tested on Windows).

## Installation

Cách cài đặt dự án

```bash
- Clone/Tải dự án về 
- Chạy file .exe
```
    

## License

[MIT](https://choosealicense.com/licenses/mit/)