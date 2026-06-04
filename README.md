# WineStore - Website Bán Rượu

Đồ án môn Công nghệ phần mềm - Spring Boot + Thymeleaf

## Công nghệ sử dụng

| Công nghệ             | Phiên bản |
| --------------------- | --------- |
| Java                  | 21        |
| Spring Boot           | 4.0.5     |
| Thymeleaf             | -         |
| Spring Security + JWT | -         |
| MySQL                 | -         |
| Maven                 | -         |

## Cấu trúc project

```
src/main/java/com/doan/WineStore/
├── config/                  # Cấu hình (Web, Data initializer)
├── controller/
│   ├── admin/api_admin/     # Controller admin (REST API + View)
│   └── client/              # Controller client (shop, auth,...)
├── dto/
│   ├── request/             # DTO request (UserCreation, CategoryUpsert, ...)
│   └── response/
│       ├── admin/           # PageResponse, OrderListItemResponse, ...
│       └── client/          # ShopProductResponse
├── entity/                  # 13 entities (User, Product, Order, ...)
├── enums/                   # PaymentStatus, Role, Status
├── exception/               # UserNotFoundException
├── repository/              # 13 repositories
├── security/                # JWT, SecurityConfig, AuthController
└── service/
    ├── admin/impl/          # Admin services
    ├── client/              # ShopService
    └── ...                  # UserService, CheckoutService, EmailService
```

### Entities (13)

- `User` - Người dùng
- `ProductEntity` - Sản phẩm (rượu vang)
- `CategoryEntity` - Danh mục
- `OrderEntity` - Đơn hàng
- `OrderItemEntity` - Chi tiết đơn hàng
- `OrderStatusHistoryEntity` - Lịch sử trạng thái
- `AddressEntity` - Địa chỉ
- `PaymentMethodEntity` - Phương thức thanh toán
- `ShippingMethodEntity` - Phương thức vận chuyển
- `DiscountCodeEntity` - Mã giảm giá
- `ReviewEntity` - Đánh giá
- `ProductImageEntity` - Hình ảnh sản phẩm
- `PasswordResetToken` - Token đặt lại mật khẩu

## Yêu cầu

- Java 21+
- MySQL 8+
- Maven

## Cài đặt

### 1. Tạo database

```sql
Sử dụng file WineStoreDB.sql có trong thư mục của đồ án
```

### 2. Cấu hình

Sửa file `src/main/resources/application.yaml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /WineStore

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/WineStore
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
```

### 3. Chạy

```bash
mvn spring-boot:run
```

Truy cập: `http://localhost:8080/WineStore`

## Tính năng

### Client

| Trang             | Đường dẫn        |
| ----------------- | ---------------- |
| Trang chủ         | `/`              |
| Cửa hàng          | `/shop`          |
| Chi tiết sản phẩm | `/detail/{id}`   |
| Giỏ hàng          | `/cart`          |
| Thanh toán        | `/checkout`      |
| Đơn hàng          | `/orders`        |
| Chi tiết đơn hàng | `/orders/{id}`   |
| Thông tin cá nhân | `/userprofile`   |
| Địa chỉ           | `/address`       |
| Đăng nhập         | `/auth/login`    |
| Đăng ký           | `/auth/register` |

### Admin

| Trang             | Đường dẫn                     |
| ----------------- | ----------------------------- |
| Dashboard         | `/admin/index`                |
| Đơn hàng          | `/admin/order/list`           |
| Chi tiết đơn hàng | `/admin/order/detail?id={id}` |
| Sản phẩm          | `/admin/product/list`         |
| Thêm sản phẩm     | `/admin/product/add`          |
| Danh mục          | `/admin/product/category`     |
| Người dùng        | `/admin/user/list`            |
| Mã giảm giá       | `/admin/marketing/discount`   |
| Đánh giá          | `/admin/reviews/review`       |
| Đăng nhập admin   | `/admin/login`                |

### API Admin (REST)

| Method | Endpoint                     | Chức năng                       |
| ------ | ---------------------------- | ------------------------------- |
| GET    | `/admin/api/orders?page=0`   | Danh sách đơn hàng (phân trang) |
| GET    | `/admin/api/orders/{id}`     | Chi tiết đơn hàng               |
| PUT    | `/admin/api/orders/{id}`     | Cập nhật đơn hàng               |
| DELETE | `/admin/api/orders/{id}`     | Xóa đơn hàng                    |
| GET    | `/admin/api/products?...`    | Danh sách sản phẩm              |
| GET    | `/admin/api/products/{id}`   | Chi tiết sản phẩm               |
| POST   | `/admin/api/products`        | Thêm sản phẩm                   |
| PUT    | `/admin/api/products/{id}`   | Cập nhật sản phẩm               |
| DELETE | `/admin/api/products/{id}`   | Xóa sản phẩm                    |
| GET    | `/admin/api/categories?...`  | Danh sách danh mục              |
| POST   | `/admin/api/categories`      | Thêm danh mục                   |
| PUT    | `/admin/api/categories/{id}` | Cập nhật danh mục               |
| DELETE | `/admin/api/categories/{id}` | Xóa danh mục                    |
| GET    | `/admin/api/discounts?...`   | Danh sách mã giảm giá           |
| POST   | `/admin/api/discounts`       | Thêm mã giảm giá                |
| PUT    | `/admin/api/discounts/{id}`  | Cập nhật mã giảm giá            |
| DELETE | `/admin/api/discounts/{id}`  | Xóa mã giảm giá                 |
| GET    | `/admin/api/reviews?...`     | Danh sách đánh giá              |
| DELETE | `/admin/api/reviews/{id}`    | Xóa đánh giá                    |
| GET    | `/admin/api/dashboard/stats` | Thống kê dashboard              |

## Quy trình đơn hàng

```
Chờ xác nhận (pending) → Xác nhận (processing) → Đang giao (shipping) → Hoàn thành (completed)
                                                                              ↓
                                                                         Đã hủy (cancelled)
```

## Note

- `ddl-auto: update` tự động tạo bảng từ entity, không cần script SQL
- Email/secret trong `application.yaml` chỉ dùng cho môi trường dev
