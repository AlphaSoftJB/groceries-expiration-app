import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  Alert,
  ActivityIndicator,
  ScrollView,
} from 'react-native';
import { useMutation, gql } from '@apollo/client';
import { SCAN_NUTRITION_LABEL } from '../graphql/nutritionQueries';

// GraphQL mutation for OCR
const PROCESS_OCR_MUTATION = gql`
  mutation ProcessOCR($imageBase64: String!) {
    processImageForOCR(imageBase64: $imageBase64) {
      name
      quantity
      expirationDate
    }
  }
`;

/**
 * Camera Screen for Photo Capture and OCR
 * 
 * Features:
 * - Take photos of food labels/receipts
 * - Extract expiration dates using OCR
 * - Auto-fill item details
 * 
 * Note: This implementation provides the UI and logic.
 * For actual camera integration, you'll need to:
 * 1. Install: npm install react-native-vision-camera
 * 2. Or use: expo-camera (if using Expo)
 * 3. Configure permissions
 */
export default function CameraScreen({ navigation, route }: any) {
  const [capturedImage, setCapturedImage] = useState<string | null>(null);
  const [isCameraActive, setIsCameraActive] = useState(false);
  const [ocrResult, setOcrResult] = useState<any>(null);
  const [scanMode, setScanMode] = useState<'expiration' | 'nutrition'>('expiration');
  
  const [scanNutritionLabel, { loading: nutritionLoading }] = useMutation(SCAN_NUTRITION_LABEL, {
    onCompleted: (data) => {
      Alert.alert(
        'Nutrition Label Scanned!',
        `Found nutrition info for: ${data.scanNutritionLabel.name || 'Unknown Item'}`,
        [
          { text: 'View Details', onPress: () => {
            // Navigate to nutrition detail or item detail
            navigation.navigate('AddItem', {
              prefillData: {
                name: data.scanNutritionLabel.name,
                nutritionData: data.scanNutritionLabel,
              },
            });
          }},
          { text: 'Retake', onPress: () => handleRetake() },
        ]
      );
    },
    onError: (err) => {
      Alert.alert('Scan Error', 'Failed to scan nutrition label: ' + err.message);
    },
  });

  const [processOCR, { loading }] = useMutation(PROCESS_OCR_MUTATION, {
    onCompleted: (data) => {
      setOcrResult(data.processImageForOCR);
      Alert.alert(
        'OCR Success!',
        `Found: ${data.processImageForOCR.name}\nExpires: ${data.processImageForOCR.expirationDate || 'N/A'}`,
        [
          { text: 'Retake', onPress: () => handleRetake() },
          {
            text: 'Use This',
            onPress: () => {
              navigation.navigate('AddItem', {
                prefillData: {
                  name: data.processImageForOCR.name,
                  quantity: data.processImageForOCR.quantity,
                  expirationDate: data.processImageForOCR.expirationDate,
                },
              });
            },
          },
        ]
      );
    },
    onError: (err) => {
      Alert.alert('OCR Error', 'Failed to process image: ' + err.message);
    },
  });

  // Simulate taking a photo
  const handleTakePhoto = () => {
    // In a real app, this would use react-native-vision-camera
    // For now, we'll use a placeholder
    const placeholderImage = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==';
    setCapturedImage(placeholderImage);
    setIsCameraActive(false);
    
    if (scanMode === 'nutrition') {
      // Simulate nutrition label scanning
      Alert.prompt(
        'Simulate Nutrition Label Scan',
        'Enter OCR text from nutrition label:',
        [
          { text: 'Cancel', style: 'cancel' },
          {
            text: 'Scan',
            onPress: (text) => {
              if (text) {
                scanNutritionLabel({ variables: { ocrText: text || 'Calories 200\nProtein 10g\nCarbs 30g' } });
              }
            },
          },
        ],
        'plain-text',
        'Calories 200\nProtein 10g\nCarbs 30g\nFat 5g'
      );
    } else {
      // Simulate expiration date OCR
      Alert.prompt(
        'Simulate OCR',
        'Enter item name for testing:',
        [
          { text: 'Cancel', style: 'cancel' },
          {
            text: 'Process',
            onPress: (text) => {
              if (text) {
                // Simulate image data
                const simulatedData = Buffer.from(text).toString('base64');
                processOCR({ variables: { imageBase64: simulatedData } });
              }
            },
          },
        ],
        'plain-text',
        'Milk'
      );
    }
  };

  const handleRetake = () => {
    setCapturedImage(null);
    setOcrResult(null);
    setIsCameraActive(true);
  };

  const handleSelectFromGallery = () => {
    // In a real app, this would use react-native-image-picker
    Alert.alert(
      'Gallery Selection',
      'This would open the image picker in a real app. For now, use the camera simulation.',
      [{ text: 'OK' }]
    );
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Camera OCR</Text>
        <Text style={styles.subtitle}>
          {scanMode === 'nutrition' ? 'Scan nutrition labels' : 'Capture expiration dates and labels'}
        </Text>
        
        {/* Scan Mode Selector */}
        <View style={styles.modeSelector}>
          <TouchableOpacity
            style={[styles.modeButton, scanMode === 'expiration' && styles.modeButtonActive]}
            onPress={() => setScanMode('expiration')}
          >
            <Text style={[styles.modeButtonText, scanMode === 'expiration' && styles.modeButtonTextActive]}>
              Expiration Date
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.modeButton, scanMode === 'nutrition' && styles.modeButtonActive]}
            onPress={() => setScanMode('nutrition')}
          >
            <Text style={[styles.modeButtonText, scanMode === 'nutrition' && styles.modeButtonTextActive]}>
              Nutrition Label
            </Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Camera View */}
      {isCameraActive ? (
        <View style={styles.cameraContainer}>
          {/*
            TODO: Integrate react-native-vision-camera here
            Example:
            <Camera
              style={styles.camera}
              device={device}
              isActive={true}
              photo={true}
            />
          */}
          <View style={styles.cameraPlaceholder}>
            <Text style={styles.cameraPlaceholderText}>Camera View</Text>
            <Text style={styles.cameraInstructions}>
              Position the expiration date in the frame
            </Text>
          </View>
          
          <View style={styles.cameraControls}>
            <TouchableOpacity
              style={styles.cancelButton}
              onPress={() => setIsCameraActive(false)}
            >
              <Text style={styles.controlButtonText}>Cancel</Text>
            </TouchableOpacity>
            
            <TouchableOpacity
              style={styles.captureButton}
              onPress={handleTakePhoto}
            >
              <View style={styles.captureButtonInner} />
            </TouchableOpacity>
            
            <TouchableOpacity
              style={styles.galleryButton}
              onPress={handleSelectFromGallery}
            >
              <Text style={styles.controlButtonText}>Gallery</Text>
            </TouchableOpacity>
          </View>
        </View>
      ) : (
        <View style={styles.section}>
          {capturedImage ? (
            <View>
              <Image
                source={{ uri: capturedImage }}
                style={styles.previewImage}
                resizeMode="contain"
              />
              
              {(loading || nutritionLoading) && (
                <View style={styles.loadingOverlay}>
                  <ActivityIndicator size="large" color="#4CAF50" />
                  <Text style={styles.loadingText}>Processing image...</Text>
                </View>
              )}
              
              {ocrResult && (
                <View style={styles.resultCard}>
                  <Text style={styles.resultTitle}>Extracted Data:</Text>
                  <Text style={styles.resultText}>Name: {ocrResult.name}</Text>
                  <Text style={styles.resultText}>Quantity: {ocrResult.quantity}</Text>
                  <Text style={styles.resultText}>
                    Expiration: {ocrResult.expirationDate || 'Not found'}
                  </Text>
                </View>
              )}
              
              <TouchableOpacity
                style={styles.retakeButton}
                onPress={handleRetake}
              >
                <Text style={styles.retakeButtonText}>Retake Photo</Text>
              </TouchableOpacity>
            </View>
          ) : (
            <View>
              <TouchableOpacity
                style={styles.startButton}
                onPress={() => setIsCameraActive(true)}
              >
                <Text style={styles.startButtonText}>📷 Open Camera</Text>
              </TouchableOpacity>
              
              <TouchableOpacity
                style={styles.gallerySelectButton}
                onPress={handleSelectFromGallery}
              >
                <Text style={styles.gallerySelectButtonText}>
                  🖼️ Select from Gallery
                </Text>
              </TouchableOpacity>
            </View>
          )}
        </View>
      )}

      {/* Instructions */}
      <View style={styles.section}>
        <Text style={styles.instructionsTitle}>How to Use:</Text>
        <Text style={styles.instructions}>
          {scanMode === 'nutrition' ? (
            `1. Select "Nutrition Label" mode\n2. Tap "Open Camera" to start\n3. Position the nutrition facts label in frame\n4. Tap the capture button\n5. Review the extracted nutrition data\n6. Confirm to save`
          ) : (
            `1. Select "Expiration Date" mode\n2. Tap "Open Camera" to start\n3. Position the expiration date clearly in frame\n4. Tap the capture button\n5. Review the extracted data\n6. Confirm to add the item`
          )}
        </Text>
      </View>

      {/* Tips */}
      <View style={styles.section}>
        <Text style={styles.instructionsTitle}>Tips for Best Results:</Text>
        <Text style={styles.instructions}>
          • Ensure good lighting{'\n'}
          • Hold the camera steady{'\n'}
          • Get close enough to read the text{'\n'}
          • Avoid glare and shadows{'\n'}
          • Keep the label flat and in focus
        </Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    backgroundColor: '#2196F3',
    padding: 20,
    paddingTop: 40,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 5,
  },
  subtitle: {
    fontSize: 14,
    color: '#fff',
    opacity: 0.9,
    marginBottom: 15,
  },
  modeSelector: {
    flexDirection: 'row',
    gap: 10,
  },
  modeButton: {
    flex: 1,
    paddingVertical: 10,
    paddingHorizontal: 15,
    borderRadius: 8,
    backgroundColor: 'rgba(255,255,255,0.2)',
    alignItems: 'center',
  },
  modeButtonActive: {
    backgroundColor: '#fff',
  },
  modeButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  modeButtonTextActive: {
    color: '#2196F3',
  },
  section: {
    backgroundColor: '#fff',
    margin: 15,
    padding: 15,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cameraContainer: {
    flex: 1,
    backgroundColor: '#000',
  },
  cameraPlaceholder: {
    height: 400,
    backgroundColor: '#000',
    justifyContent: 'center',
    alignItems: 'center',
  },
  cameraPlaceholderText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: 'bold',
  },
  cameraInstructions: {
    color: '#fff',
    fontSize: 14,
    marginTop: 10,
    opacity: 0.8,
  },
  cameraControls: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#000',
  },
  captureButton: {
    width: 70,
    height: 70,
    borderRadius: 35,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 4,
    borderColor: '#ddd',
  },
  captureButtonInner: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: '#fff',
  },
  cancelButton: {
    padding: 10,
  },
  galleryButton: {
    padding: 10,
  },
  controlButtonText: {
    color: '#fff',
    fontSize: 16,
  },
  startButton: {
    backgroundColor: '#2196F3',
    padding: 20,
    borderRadius: 10,
    alignItems: 'center',
    marginBottom: 15,
  },
  startButtonText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: 'bold',
  },
  gallerySelectButton: {
    backgroundColor: '#9C27B0',
    padding: 20,
    borderRadius: 10,
    alignItems: 'center',
  },
  gallerySelectButtonText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: 'bold',
  },
  previewImage: {
    width: '100%',
    height: 300,
    backgroundColor: '#f0f0f0',
    borderRadius: 10,
    marginBottom: 15,
  },
  loadingOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.7)',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 10,
  },
  loadingText: {
    color: '#fff',
    marginTop: 10,
    fontSize: 16,
  },
  resultCard: {
    backgroundColor: '#E8F5E9',
    padding: 15,
    borderRadius: 10,
    marginBottom: 15,
    borderLeftWidth: 4,
    borderLeftColor: '#4CAF50',
  },
  resultTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#2E7D32',
  },
  resultText: {
    fontSize: 14,
    color: '#333',
    marginBottom: 5,
  },
  retakeButton: {
    backgroundColor: '#FF9800',
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
  },
  retakeButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  instructionsTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#333',
  },
  instructions: {
    fontSize: 14,
    color: '#666',
    lineHeight: 22,
  },
});
