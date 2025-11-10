import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  ScrollView,
  Dimensions,
} from 'react-native';
import {
  AllergenAlert,
  getAllergenDisplayName,
  getSeverityColor,
  Severity,
} from '../graphql/nutritionQueries';
import { useTranslation } from 'react-i18next';

interface AllergenAlertDialogProps {
  visible: boolean;
  alerts: AllergenAlert[];
  onAction: (action: 'proceed' | 'cancel') => void;
}

/**
 * Allergen Alert Dialog Component
 * Shows warnings when scanned items contain user's allergens
 * Minimal UI Implementation
 */
const AllergenAlertDialog: React.FC<AllergenAlertDialogProps> = ({
  visible,
  alerts,
  onAction,
}) => {
  const { t } = useTranslation();

  if (!alerts || alerts.length === 0) {
    return null;
  }

  // Find the highest severity level
  const highestSeverity = alerts.reduce((max, alert) => {
    if (!alert.severity) return max;
    const severityOrder = {
      [Severity.MILD]: 1,
      [Severity.MODERATE]: 2,
      [Severity.SEVERE]: 3,
      [Severity.LIFE_THREATENING]: 4,
    };
    const currentLevel = severityOrder[alert.severity] || 0;
    const maxLevel = max ? severityOrder[max] || 0 : 0;
    return currentLevel > maxLevel ? alert.severity : max;
  }, alerts[0]?.severity);

  const severityColor = highestSeverity ? getSeverityColor(highestSeverity) : '#FFC107';
  const isLifeThreatening = highestSeverity === Severity.LIFE_THREATENING;
  const isSevere = highestSeverity === Severity.SEVERE || isLifeThreatening;

  return (
    <Modal
      visible={visible}
      animationType="fade"
      transparent={true}
      onRequestClose={() => onAction('cancel')}
    >
      <View style={styles.overlay}>
        <View style={[styles.container, isSevere && styles.containerSevere]}>
          {/* Header */}
          <View style={[styles.header, { backgroundColor: severityColor }]}>
            <Text style={styles.headerIcon}>
              {isLifeThreatening ? '🚨' : isSevere ? '⚠️' : 'ℹ️'}
            </Text>
            <Text style={styles.headerTitle}>
              {isLifeThreatening
                ? t('allergen_alert_life_threatening')
                : isSevere
                ? t('allergen_alert_severe')
                : t('allergen_alert')}
            </Text>
          </View>

          {/* Alert List */}
          <ScrollView style={styles.content}>
            <Text style={styles.message}>
              {isLifeThreatening
                ? t('this_product_contains_life_threatening_allergens')
                : isSevere
                ? t('this_product_contains_severe_allergens')
                : t('this_product_contains_allergens_you_marked')}
            </Text>

            {alerts.map((alert, index) => {
              const displayName = getAllergenDisplayName(
                alert.allergenType,
                alert.allergenName || undefined
              );
              const alertColor = alert.severity ? getSeverityColor(alert.severity) : '#FFC107';

              return (
                <View key={index} style={styles.alertItem}>
                  <View style={[styles.alertIndicator, { backgroundColor: alertColor }]} />
                  <View style={styles.alertContent}>
                    <Text style={styles.alertName}>{displayName}</Text>
                    {alert.severity && (
                      <Text style={styles.alertSeverity}>
                        {t('severity')}: {alert.severity}
                      </Text>
                    )}
                  </View>
                </View>
              );
            })}

            {isLifeThreatening && (
              <View style={styles.warningBox}>
                <Text style={styles.warningText}>
                  ⚠️ {t('consuming_this_product_may_cause_severe_reaction')}
                </Text>
              </View>
            )}
          </ScrollView>

          {/* Action Buttons */}
          <View style={styles.buttons}>
            <TouchableOpacity
              style={[styles.button, styles.cancelButton]}
              onPress={() => onAction('cancel')}
            >
              <Text style={styles.cancelButtonText}>
                {t('cancel')}
              </Text>
            </TouchableOpacity>
            
            {!isLifeThreatening && (
              <TouchableOpacity
                style={[styles.button, styles.proceedButton]}
                onPress={() => onAction('proceed')}
              >
                <Text style={styles.proceedButtonText}>
                  {t('proceed_anyway')}
                </Text>
              </TouchableOpacity>
            )}
          </View>

          {isLifeThreatening && (
            <Text style={styles.disclaimer}>
              {t('for_your_safety_we_recommend_not_consuming_this_product')}
            </Text>
          )}
        </View>
      </View>
    </Modal>
  );
};

const { width } = Dimensions.get('window');

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  container: {
    backgroundColor: '#FFF',
    borderRadius: 16,
    width: Math.min(width - 40, 400),
    maxHeight: '80%',
    overflow: 'hidden',
  },
  containerSevere: {
    borderWidth: 3,
    borderColor: '#F44336',
  },
  header: {
    padding: 20,
    alignItems: 'center',
  },
  headerIcon: {
    fontSize: 48,
    marginBottom: 8,
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFF',
    textAlign: 'center',
  },
  content: {
    padding: 20,
    maxHeight: 300,
  },
  message: {
    fontSize: 16,
    color: '#333',
    marginBottom: 20,
    textAlign: 'center',
    lineHeight: 22,
  },
  alertItem: {
    flexDirection: 'row',
    backgroundColor: '#F5F5F5',
    borderRadius: 8,
    padding: 12,
    marginBottom: 12,
    alignItems: 'center',
  },
  alertIndicator: {
    width: 6,
    height: 40,
    borderRadius: 3,
    marginRight: 12,
  },
  alertContent: {
    flex: 1,
  },
  alertName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  alertSeverity: {
    fontSize: 14,
    color: '#666',
  },
  warningBox: {
    backgroundColor: '#FFF3CD',
    borderLeftWidth: 4,
    borderLeftColor: '#F44336',
    padding: 12,
    borderRadius: 8,
    marginTop: 12,
  },
  warningText: {
    fontSize: 14,
    color: '#856404',
    fontWeight: '600',
  },
  buttons: {
    flexDirection: 'row',
    padding: 16,
    gap: 12,
    borderTopWidth: 1,
    borderTopColor: '#E0E0E0',
  },
  button: {
    flex: 1,
    padding: 14,
    borderRadius: 8,
    alignItems: 'center',
  },
  cancelButton: {
    backgroundColor: '#F44336',
  },
  cancelButtonText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '600',
  },
  proceedButton: {
    backgroundColor: '#F5F5F5',
    borderWidth: 1,
    borderColor: '#E0E0E0',
  },
  proceedButtonText: {
    color: '#666',
    fontSize: 16,
    fontWeight: '600',
  },
  disclaimer: {
    fontSize: 12,
    color: '#999',
    textAlign: 'center',
    paddingHorizontal: 20,
    paddingBottom: 16,
    fontStyle: 'italic',
  },
});

export default AllergenAlertDialog;
