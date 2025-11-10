# New Features Implementation TODO

## Phase 1: Barcode Scanning

### Backend
- [x] Add barcode scanning endpoint to GraphQL schema
- [x] Integrate with product database API (Open Food Facts)
- [x] Create BarcodeService.java for barcode lookup
- [x] Add barcode field to Item entity
- [x] Create mutation: scanBarcode(barcode: String!)

### Frontend (React Native)
- [x] Create BarcodeScannerScreen.tsx
- [x] Integrate with GraphQL mutation
- [x] Add barcode scanner button to AddItemScreen
- [x] Handle barcode scan results
- [x] Auto-fill item details from barcode data
- [ ] Install react-native-vision-camera (requires native setup)
- [ ] Configure camera permissions (AndroidManifest.xml, Info.plist)

## Phase 2: Camera Integration for OCR

### Backend
- [ ] Update OCR endpoint to accept image files (not just base64)
- [ ] Add file upload handling
- [ ] Improve OCR date extraction accuracy
- [ ] Add support for multiple image formats

### Frontend (React Native)
- [ ] Create CameraScreen.tsx for photo capture
- [ ] Add image picker for gallery selection
- [ ] Implement image upload to backend
- [ ] Display OCR results in AddItemScreen
- [ ] Add camera button to AddItemScreen
- [ ] Handle image compression before upload

## Phase 3: Push Notifications

### Backend
- [ ] Add Firebase Admin SDK dependency to pom.xml
- [ ] Create NotificationService.java
- [ ] Add FCM token field to User entity
- [ ] Create mutation: registerDeviceToken(token: String!)
- [ ] Implement scheduled job to check expiring items
- [ ] Send notifications for items expiring in 1, 3, 7 days
- [ ] Add notification preferences to User entity
- [ ] Create notification history tracking

### Frontend (React Native)
- [ ] Install @react-native-firebase/app
- [ ] Install @react-native-firebase/messaging
- [ ] Configure Firebase project
- [ ] Request notification permissions
- [ ] Register FCM token with backend
- [ ] Handle foreground notifications
- [ ] Handle background notifications
- [ ] Add notification settings screen
- [ ] Test notification delivery

## Phase 4: Testing & Integration

- [ ] Test barcode scanning with real products
- [ ] Test OCR with various expiration date formats
- [ ] Test push notifications on iOS
- [ ] Test push notifications on Android
- [ ] Test all features together in workflow
- [ ] Performance testing
- [ ] Error handling and edge cases

## Phase 5: Documentation

- [ ] Update README with new features
- [ ] Add setup instructions for Firebase
- [ ] Document barcode scanning API
- [ ] Document notification system
- [ ] Update mobile app screenshots
- [ ] Create user guide for new features
