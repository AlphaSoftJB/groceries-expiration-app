import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Modal,
  ActivityIndicator,
  Alert,
  Image,
} from 'react-native';
import { useMutation } from '@apollo/client';
import { Camera, CameraType } from 'expo-camera';
import * as ImageManipulator from 'expo-image-manipulator';
import { SCAN_NUTRITION_LABEL, NutritionScanResult } from '../graphql/nutritionQueries';
import { useTranslation } from 'react-i18next';
import AllergenAlertDialog from './AllergenAlertDialog';

interface NutritionScannerProps {
  itemId: string;
  onScanComplete: (result: NutritionScanResult) => void;
}

/**
 * Nutrition Scanner Component
 * Allows users to scan nutrition labels using camera + OCR
 * Minimal UI Implementation
 */
const NutritionScanner: React.FC<NutritionScannerProps> = ({ itemId, onScanComplete }) => {
  const { t } = useTranslation();
  const [cameraVisible, setCameraVisible] = useState(false);
  const [hasPermission, setHasPermission] = useState<boolean | null>(null);
  const [processing, setProcessing] = useState(false);
  const [scanResult, setScanResult] = useState<NutritionScanResult | null>(null);
  const [alertsVisible, setAlertsVisible] = useState(false);
  
  let cameraRef: Camera | null = null;

  const [scanNutritionLabel] = useMutation(SCAN_NUTRITION_LABEL, {
    onCompleted: (data) => {
      const result = data.scanNutritionLabel;
      setScanResult(result);
      
      if (result.success) {
        // Check if there are allergen alerts
        if (result.allergenAlerts && result.allergenAlerts.length > 0) {
          setAlertsVisible(true);
        } else {
          Alert.alert(
            t('success'),
            t('nutrition_label_scanned_successfully'),
            [{ text: t('ok'), onPress: () => onScanComplete(result) }]
          );
        }
      } else {
        Alert.alert(t('error'), result.message || t('failed_to_scan_label'));
      }
      
      setProcessing(false);
      setCameraVisible(false);
    },
    onError: (error) => {
      Alert.alert(t('error'), error.message);
      setProcessing(false);
      setCameraVisible(false);
    },
  });

  const requestCameraPermission = async () => {
    const { status } = await Camera.requestCameraPermissionsAsync();
    setHasPermission(status === 'granted');
    return status === 'granted';
  };

  const handleOpenCamera = async () => {
    const granted = await requestCameraPermission();
    if (granted) {
      setCameraVisible(true);
    } else {
      Alert.alert(
        t('permission_required'),
        t('camera_permission_required_for_scanning')
      );
    }
  };

  const handleTakePicture = async () => {
    if (!cameraRef) return;

    try {
      setProcessing(true);
      
      // Take picture
      const photo = await cameraRef.takePictureAsync({
        quality: 0.8,
        base64: true,
      });

      // Resize image for better OCR performance
      const manipulatedImage = await ImageManipulator.manipulateAsync(
        photo.uri,
        [{ resize: { width: 1024 } }],
        { compress: 0.8, format: ImageManipulator.SaveFormat.JPEG }
      );

      // In a real implementation, you would:
      // 1. Upload image to server
      // 2. Server runs OCR (using Tesseract, Google Vision API, etc.)
      // 3. Server returns OCR text
      // 4. Pass OCR text to scanNutritionLabel mutation

      // For this minimal implementation, we'll simulate OCR
      // In production, replace this with actual OCR service
      const mockOCRText = await performOCR(manipulatedImage.uri);

      // Call GraphQL mutation with OCR text
      await scanNutritionLabel({
        variables: {
          itemId,
          ocrText: mockOCRText,
        },
      });

    } catch (error) {
      console.error('Error taking picture:', error);
      Alert.alert(t('error'), t('failed_to_capture_image'));
      setProcessing(false);
      setCameraVisible(false);
    }
  };

  // Mock OCR function - Replace with actual OCR service in production
  const performOCR = async (imageUri: string): Promise<string> => {
    // TODO: Implement actual OCR
    // Options:
    // 1. Use Google Cloud Vision API
    // 2. Use AWS Textract
    // 3. Use Tesseract.js
    // 4. Use custom OCR service
    
    // For now, return mock nutrition label text
    return `
      Nutrition Facts
      Serving Size: 1 cup (240ml)
      Servings Per Container: 8
      
      Amount Per Serving
      Calories: 150
      Calories from Fat: 70
      
      % Daily Value*
      Total Fat 8g - 12%
        Saturated Fat 5g - 25%
        Trans Fat 0g
      Cholesterol 30mg - 10%
      Sodium 125mg - 5%
      Total Carbohydrate 12g - 4%
        Dietary Fiber 0g - 0%
        Sugars 12g
      Protein 8g
      
      Vitamin D 2.5mcg - 13%
      Calcium 300mg - 23%
      Iron 0mg - 0%
      Potassium 380mg - 8%
      
      Ingredients: Milk, Vitamin D3
    `;
  };

  const handleAlertAction = (action: 'proceed' | 'cancel') => {
    setAlertsVisible(false);
    
    if (action === 'proceed' && scanResult) {
      onScanComplete(scanResult);
    } else {
      Alert.alert(t('scan_cancelled'), t('nutrition_info_not_saved'));
    }
  };

  return (
    <View>
      {/* Scan Button */}
      <TouchableOpacity
        style={styles.scanButton}
        onPress={handleOpenCamera}
        disabled={processing}
      >
        <Text style={styles.scanButtonText}>
          📷 {t('scan_nutrition_label')}
        </Text>
      </TouchableOpacity>

      {/* Camera Modal */}
      <Modal
        visible={cameraVisible}
        animationType="slide"
        onRequestClose={() => !processing && setCameraVisible(false)}
      >
        <View style={styles.cameraContainer}>
          {hasPermission === false ? (
            <View style={styles.permissionDenied}>
              <Text style={styles.permissionText}>
                {t('camera_permission_denied')}
              </Text>
            </View>
          ) : (
            <>
              <Camera
                ref={(ref) => (cameraRef = ref)}
                style={styles.camera}
                type={CameraType.back}
              />
              
              {/* Overlay with instructions */}
              <View style={styles.overlay}>
                <View style={styles.instructionBox}>
                  <Text style={styles.instructionText}>
                    {t('align_nutrition_label_in_frame')}
                  </Text>
                </View>
                
                {/* Frame guide */}
                <View style={styles.frameGuide} />
                
                {/* Capture button */}
                <View style={styles.captureButtonContainer}>
                  {processing ? (
                    <ActivityIndicator size="large" color="#FFF" />
                  ) : (
                    <>
                      <TouchableOpacity
                        style={styles.captureButton}
                        onPress={handleTakePicture}
                      >
                        <View style={styles.captureButtonInner} />
                      </TouchableOpacity>
                      <TouchableOpacity
                        style={styles.cancelButton}
                        onPress={() => setCameraVisible(false)}
                      >
                        <Text style={styles.cancelButtonText}>
                          {t('cancel')}
                        </Text>
                      </TouchableOpacity>
                    </>
                  )}
                </View>
              </View>
            </>
          )}
        </View>
      </Modal>

      {/* Allergen Alert Dialog */}
      {scanResult && (
        <AllergenAlertDialog
          visible={alertsVisible}
          alerts={scanResult.allergenAlerts}
          onAction={handleAlertAction}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  scanButton: {
    backgroundColor: '#007AFF',
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
    marginVertical: 8,
  },
  scanButtonText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '600',
  },
  cameraContainer: {
    flex: 1,
    backgroundColor: '#000',
  },
  camera: {
    flex: 1,
  },
  overlay: {
    ...StyleSheet.absoluteFillObject,
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 40,
  },
  instructionBox: {
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    padding: 16,
    borderRadius: 8,
    marginHorizontal: 20,
  },
  instructionText: {
    color: '#FFF',
    fontSize: 16,
    textAlign: 'center',
  },
  frameGuide: {
    width: '80%',
    height: '50%',
    borderWidth: 2,
    borderColor: '#FFF',
    borderRadius: 12,
    borderStyle: 'dashed',
  },
  captureButtonContainer: {
    alignItems: 'center',
  },
  captureButton: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 16,
  },
  captureButtonInner: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: '#FFF',
  },
  cancelButton: {
    paddingHorizontal: 24,
    paddingVertical: 12,
  },
  cancelButtonText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '600',
  },
  permissionDenied: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 40,
  },
  permissionText: {
    color: '#FFF',
    fontSize: 18,
    textAlign: 'center',
  },
});

export default NutritionScanner;
