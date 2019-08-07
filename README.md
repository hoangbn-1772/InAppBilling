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
 - Các bước để tích hợp Google Play Billing vào trong ứng dụng
### Step 1: Update your app's dependencies
 
<img src="dependencies.png"/>
 
### Step 2: Connect to Google Play
- Tạo một instance cho **BillingClient**.
- Phải gọi **setListener()** thông qua **PurchasesUpdatedListener** để nhận các cập nhật về các giao dịch mua được tạo bởi app, cũng như do Google Play Store khởi tạo.
- Implement **BillingClientStateListener** để nhận một callback sau khi thực hiện quá trình kết nối.

<img src="connect_to_GP.png"/>
 
### Query for in-app product details.
- Query Google Play để lấy thông tin chi tiết product gọi **querySkuDetailsAsync()**.
- Tham số truyền vào là một **SkuDetailsParams** chỉ định một danh sách chuỗi product ID và **SkuType** (INAPP, SUBS).
 
 <img src="query_product_detail.png"/>
 
- Xử lý kết quả trả về trong **onSkuDetailsResponse()** bằng cách implements **SkuDetailsResponseListener**. Kiểm tra **responseCode** xem kết quả trả về:
    + Thành công (BillingResponse.OK): Trả về một danh sách các đối tượng SkuDetails
    + Nếu xảy ra lỗi: có thể sử dụng **getDebugMessage** để xem thông tin lỗi.
- Tham khảo thêm responseCode: <a href="/reference/com/android/billingclient/api/BillingClient.BillingResponse"><code translate="no" dir="ltr">BillingClient.BillingResponse</code></a>
    
<img src="handle_query-result.png"/>

### Step 3: Enable the purchase of an in-app product
- Một số thiết bị Android có version cũ hơn Google Play Store sẽ không hỗ trợ một số product nhất định (như là subscriptions). Vì thế, trước khi billing flow hãy kiểm tra xem device có hỗ trợ các sản phẩm bạn muốn bán không **isFeatureSupported()**
- Gọi **launchBillingFlow()** từ UI thread để bắt đầu yêu cầu mua từ app. Tham số truyền vào là một **BillingFlowParams**

<img src="enable_purchase.png"/>

- Method **launchBillingFlow** trả về một danh sách **responseCode** và một danh sách đối tượng **Purchase** trong method **onPurchasesUpdated** được override lại từ **PurchasesUpdatedListener**

<img src="billing_flow_response.png"/>

# Source
 - Android Developer: https://developer.android.com/google/play/billing/billing_library_overview#acknowledge
 - Medium:
  + https://medium.com/@vleonovs8/tutorial-google-play-billing-in-app-purchases-6143bda8d290
  + https://medium.com/exploring-android/exploring-the-play-billing-library-for-android-55321f282929
