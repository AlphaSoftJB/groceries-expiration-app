import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  ScrollView,
} from 'react-native';
import { useMutation, gql } from '@apollo/client';

// GraphQL mutation for barcode scanning
const SCAN_BARCODE_MUTATION = gql`
  mutation ScanBarcode($barcode: String!) {
    scanBarcode(barcode: $barcode) {
      success
      message
      barcode
      name
      brand
      category
      imageUrl
      storageLocation
      estimatedShelfLifeDays
    }
  }
`;

/**
 * Barcode Scanner Screen
 * 
 * Features:
 * - Camera-based barcode scanning (requires react-native-vision-camera)
 * - Manual barcode entry option
 * - Product lookup via Open Food Facts API
 * - Auto-fill item details
 * 
 * Note: This implementation provides the UI and logic.
 * For actual camera integration, you'll need to:
 * 1. Install: npm install react-native-vision-camera
 * 2. Configure permissions in AndroidManifest.xml and Info.plist
 * 3. Link native modules: npx pod-install (iOS)
 */
export default function BarcodeScannerScreen({ navigation, route }: any) {
  const [manualBarcode, setManualBarcode] = useState('');
  const [scannedProduct, setScannedProduct] = useState<any>(null);
  const [isCameraActive, setIsCameraActive] = useState(false);
  
  const [scanBarcode, { loading, error }] = useMutation(SCAN_BARCODE_MUTATION, {
    onCompleted: (data) => {
      if (data.scanBarcode.success) {
        setScannedProduct(data.scanBarcode);
        Alert.alert(
          'Product Found!',
          `${data.scanBarcode.name}\n\nWould you like to add this item to your inventory?`,
          [
            { text: 'Cancel', style: 'cancel' },
            {
              text: 'Add Item',
              onPress: () => {
                // Navigate to AddItemScreen with pre-filled data
                navigation.navigate('AddItem', {
                  prefillData: {
                    name: data.scanBarcode.name,
                    barcode: data.scanBarcode.barcode,
                    storageLocation: data.scanBarcode.storageLocation,
                    estimatedShelfLifeDays: data.scanBarcode.estimatedShelfLifeDays,
                  },
                });
              },
            },
          ]
        );
      } else {
        Alert.alert('Product Not Found', data.scanBarcode.message);
      }
    },
    onError: (err) => {
      Alert.alert('Error', 'Failed to scan barcode: ' + err.message);
    },
  });

  // Handle barcode scan from camera
  const handleBarcodeScanned = (barcode: string) => {
    setIsCameraActive(false);
    scanBarcode({ variables: { barcode } });
  };

  // Handle manual barcode entry
  const handleManualScan = () => {
    if (manualBarcode.trim().length === 0) {
      Alert.alert('Error', 'Please enter a barcode');
      return;
    }
    scanBarcode({ variables: { barcode: manualBarcode.trim() } });
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Barcode Scanner</Text>
        <Text style={styles.subtitle}>
          Scan product barcodes to auto-fill item details
        </Text>
      </View>

      {/* Camera Scanner Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Camera Scanner</Text>
        
        {isCameraActive ? (
          <View style={styles.cameraContainer}>
            {/* 
              TODO: Integrate react-native-vision-camera here
              Example:
              <Camera
                style={styles.camera}
                device={device}
                isActive={true}
                codeScanner={{
                  codeTypes: ['ean-13', 'ean-8', 'upc-a', 'upc-e', 'code-128'],
                  onCodeScanned: (codes) => {
                    if (codes.length > 0) {
                      handleBarcodeScanned(codes[0].value);
                    }
                  },
                }}
              />
            */}
            <View style={styles.cameraPlaceholder}>
              <Text style={styles.cameraPlaceholderText}>
                Camera View
              </Text>
              <Text style={styles.cameraInstructions}>
                Point camera at barcode
              </Text>
            </View>
            <TouchableOpacity
              style={styles.cancelButton}
              onPress={() => setIsCameraActive(false)}
            >
              <Text style={styles.cancelButtonText}>Cancel</Text>
            </TouchableOpacity>
          </View>
        ) : (
          <TouchableOpacity
            style={styles.scanButton}
            onPress={() => setIsCameraActive(true)}
          >
            <Text style={styles.scanButtonText}>📷 Open Camera Scanner</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* Manual Entry Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Manual Entry</Text>
        <View style={styles.manualInputContainer}>
          <input
            style={styles.input}
            placeholder="Enter barcode number"
            value={manualBarcode}
            onChange={(e: any) => setManualBarcode(e.target.value)}
            keyboardType="numeric"
          />
          <TouchableOpacity
            style={styles.manualScanButton}
            onPress={handleManualScan}
            disabled={loading}
          >
            {loading ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <Text style={styles.manualScanButtonText}>Scan</Text>
            )}
          </TouchableOpacity>
        </View>
      </View>

      {/* Scanned Product Display */}
      {scannedProduct && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Scanned Product</Text>
          <View style={styles.productCard}>
            <Text style={styles.productName}>{scannedProduct.name}</Text>
            {scannedProduct.brand && (
              <Text style={styles.productBrand}>Brand: {scannedProduct.brand}</Text>
            )}
            {scannedProduct.category && (
              <Text style={styles.productDetail}>Category: {scannedProduct.category}</Text>
            )}
            <Text style={styles.productDetail}>
              Storage: {scannedProduct.storageLocation}
            </Text>
            <Text style={styles.productDetail}>
              Estimated Shelf Life: {scannedProduct.estimatedShelfLifeDays} days
            </Text>
            <Text style={styles.productBarcode}>
              Barcode: {scannedProduct.barcode}
            </Text>
          </View>
        </View>
      )}

      {/* Instructions */}
      <View style={styles.section}>
        <Text style={styles.instructionsTitle}>How to Use:</Text>
        <Text style={styles.instructions}>
          1. Use the camera scanner to scan product barcodes{'\n'}
          2. Or enter the barcode number manually{'\n'}
          3. Product details will be fetched automatically{'\n'}
          4. Review and add the item to your inventory
        </Text>
      </View>

      {/* Test Barcodes */}
      <View style={styles.section}>
        <Text style={styles.instructionsTitle}>Test Barcodes:</Text>
        <TouchableOpacity onPress={() => setManualBarcode('3017620422003')}>
          <Text style={styles.testBarcode}>• 3017620422003 (Nutella)</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => setManualBarcode('5449000000996')}>
          <Text style={styles.testBarcode}>• 5449000000996 (Coca-Cola)</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => setManualBarcode('7622210449283')}>
          <Text style={styles.testBarcode}>• 7622210449283 (Milka Chocolate)</Text>
        </TouchableOpacity>
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
    backgroundColor: '#4CAF50',
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
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#333',
  },
  cameraContainer: {
    alignItems: 'center',
  },
  cameraPlaceholder: {
    width: '100%',
    height: 300,
    backgroundColor: '#000',
    borderRadius: 10,
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
  scanButton: {
    backgroundColor: '#4CAF50',
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
  },
  scanButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  cancelButton: {
    marginTop: 15,
    padding: 10,
    backgroundColor: '#f44336',
    borderRadius: 10,
    paddingHorizontal: 30,
  },
  cancelButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  manualInputContainer: {
    flexDirection: 'row',
    gap: 10,
  },
  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    padding: 12,
    fontSize: 16,
  },
  manualScanButton: {
    backgroundColor: '#2196F3',
    paddingHorizontal: 25,
    borderRadius: 10,
    justifyContent: 'center',
    minWidth: 80,
  },
  manualScanButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  productCard: {
    backgroundColor: '#f9f9f9',
    padding: 15,
    borderRadius: 10,
    borderLeftWidth: 4,
    borderLeftColor: '#4CAF50',
  },
  productName: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#333',
  },
  productBrand: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  productDetail: {
    fontSize: 14,
    color: '#666',
    marginBottom: 3,
  },
  productBarcode: {
    fontSize: 12,
    color: '#999',
    marginTop: 8,
    fontFamily: 'monospace',
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
  testBarcode: {
    fontSize: 14,
    color: '#2196F3',
    marginBottom: 8,
    textDecorationLine: 'underline',
  },
});
