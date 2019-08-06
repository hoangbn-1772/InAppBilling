# Google Play Billing
- Google Play Billing là một dịch vụ bán nội dung số trên Android.

## In-app product types
- One-time products: ví dụ game levels, media files...
- Rewarded products: như là extra lives, tiền tệ trong game,...
- Subscriptions: như là tạp chí online, music streaming services,...

## Purchase tokens and order IDs
- Purchase token: là một chuỗi đại diện cho quyền lợi cuả người mua đối với sản phẩm trên Google Play, nó chỉ ra rằng người dùng đã thanh toán cho một sản phẩm cụ thể, đại diện bởi SKU.
- Order ID: là một chuỗi đại diện cho một giao dịch tài chính trên Google Play. Chuỗi này được kèm trong biên lai được gửi qua email tới người mua và nhà phát triển bên thứ 3 sử dụng oder ID để quản lý tiền hoàn lại trong Order Manager của Google Play Store.Ngoài ra, nó cũng được sử dụng trong sales và payout reports.

## In-app product configuaration options
- Title: mô tả ngắn về sản phẩm trong ứng dụng.
- Description: mô tả chi tiết về sản phẩm trong ứng dụng.
- Product ID: là id duy nhất định danh product, nó còn được gọi là SKUs trong Google Play Billing Library.
- Price/ Default Price: Số tiền mà người dùng phải trả cho product trong ứng dụng
  + One-time product: Giá mặc định được tính cho khách hàng mỗi lần cho mua product.
  + Rewarded purchases: Không phải trả phí thay vào đó người dùng xem quảng cáo
  + Subscription: Giá mà khách hàng phải trả theo định kỳ.
 - Tham khảo thêm <a href="https://developer.android.com/google/play/billing/billing_overview#unique-one-time-product-configuration-options">tại đây</a>
 
 # Implement Google Play Billing
