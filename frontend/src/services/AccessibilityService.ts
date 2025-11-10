import { AccessibilityInfo, Platform } from 'react-native';
import Voice from '@react-native-voice/voice';
import * as Speech from 'expo-speech';

/**
 * Accessibility Service
 * Provides comprehensive accessibility features including:
 * - Screen reader support
 * - Voice commands
 * - Text-to-speech
 * - High contrast mode
 * - Large text support
 * - Color blind friendly modes
 */

export interface AccessibilitySettings {
  screenReaderEnabled: boolean;
  voiceCommandsEnabled: boolean;
  highContrastMode: boolean;
  largeTextMode: boolean;
  colorBlindMode: 'none' | 'protanopia' | 'deuteranopia' | 'tritanopia';
  audioFeedbackEnabled: boolean;
}

class AccessibilityService {
  private settings: AccessibilitySettings = {
    screenReaderEnabled: false,
    voiceCommandsEnabled: false,
    highContrastMode: false,
    largeTextMode: false,
    colorBlindMode: 'none',
    audioFeedbackEnabled: true,
  };

  private voiceCommandCallbacks: Map<string, () => void> = new Map();

  constructor() {
    this.initializeVoiceRecognition();
    this.checkScreenReaderStatus();
  }

  /**
   * Initialize voice recognition
   */
  private async initializeVoiceRecognition() {
    try {
      Voice.onSpeechResults = this.onSpeechResults.bind(this);
      Voice.onSpeechError = this.onSpeechError.bind(this);
    } catch (error) {
      console.error('Error initializing voice recognition:', error);
    }
  }

  /**
   * Check if screen reader is enabled
   */
  private async checkScreenReaderStatus() {
    try {
      const screenReaderEnabled = await AccessibilityInfo.isScreenReaderEnabled();
      this.settings.screenReaderEnabled = screenReaderEnabled;
    } catch (error) {
      console.error('Error checking screen reader status:', error);
    }
  }

  /**
   * Update accessibility settings
   */
  public updateSettings(newSettings: Partial<AccessibilitySettings>) {
    this.settings = { ...this.settings, ...newSettings };
  }

  /**
   * Get current accessibility settings
   */
  public getSettings(): AccessibilitySettings {
    return { ...this.settings };
  }

  /**
   * Announce text for screen readers
   */
  public announce(text: string, priority: 'polite' | 'assertive' = 'polite') {
    if (this.settings.screenReaderEnabled) {
      AccessibilityInfo.announceForAccessibility(text);
    }
    
    // Also provide audio feedback if enabled
    if (this.settings.audioFeedbackEnabled) {
      this.speak(text);
    }
  }

  /**
   * Speak text using text-to-speech
   */
  public async speak(text: string, options?: {
    language?: string;
    pitch?: number;
    rate?: number;
  }) {
    try {
      await Speech.speak(text, {
        language: options?.language || 'en-US',
        pitch: options?.pitch || 1.0,
        rate: options?.rate || 1.0,
      });
    } catch (error) {
      console.error('Error speaking text:', error);
    }
  }

  /**
   * Stop speaking
   */
  public async stopSpeaking() {
    try {
      await Speech.stop();
    } catch (error) {
      console.error('Error stopping speech:', error);
    }
  }

  /**
   * Start listening for voice commands
   */
  public async startVoiceCommands() {
    if (!this.settings.voiceCommandsEnabled) {
      return;
    }

    try {
      await Voice.start('en-US');
      this.announce('Voice commands activated. Say a command.', 'assertive');
    } catch (error) {
      console.error('Error starting voice commands:', error);
    }
  }

  /**
   * Stop listening for voice commands
   */
  public async stopVoiceCommands() {
    try {
      await Voice.stop();
    } catch (error) {
      console.error('Error stopping voice commands:', error);
    }
  }

  /**
   * Register a voice command
   */
  public registerVoiceCommand(command: string, callback: () => void) {
    this.voiceCommandCallbacks.set(command.toLowerCase(), callback);
  }

  /**
   * Unregister a voice command
   */
  public unregisterVoiceCommand(command: string) {
    this.voiceCommandCallbacks.delete(command.toLowerCase());
  }

  /**
   * Handle speech results
   */
  private onSpeechResults(event: any) {
    const results = event.value;
    if (results && results.length > 0) {
      const spokenText = results[0].toLowerCase();
      
      // Check for registered commands
      for (const [command, callback] of this.voiceCommandCallbacks.entries()) {
        if (spokenText.includes(command)) {
          callback();
          this.announce(`Executing command: ${command}`, 'assertive');
          return;
        }
      }
      
      this.announce('Command not recognized. Please try again.', 'polite');
    }
  }

  /**
   * Handle speech errors
   */
  private onSpeechError(event: any) {
    console.error('Speech recognition error:', event.error);
  }

  /**
   * Get color scheme for color blind mode
   */
  public getColorScheme() {
    switch (this.settings.colorBlindMode) {
      case 'protanopia':
        return {
          primary: '#0066CC',
          secondary: '#FFB300',
          success: '#0088CC',
          warning: '#FF8800',
          danger: '#CC0000',
          fresh: '#0088CC',
          expiringSoon: '#FFB300',
          expired: '#CC0000',
        };
      case 'deuteranopia':
        return {
          primary: '#0066CC',
          secondary: '#FFB300',
          success: '#0088CC',
          warning: '#FF8800',
          danger: '#CC0000',
          fresh: '#0088CC',
          expiringSoon: '#FFB300',
          expired: '#CC0000',
        };
      case 'tritanopia':
        return {
          primary: '#CC0066',
          secondary: '#00CCCC',
          success: '#00AAAA',
          warning: '#CC6600',
          danger: '#CC0066',
          fresh: '#00AAAA',
          expiringSoon: '#CC6600',
          expired: '#CC0066',
        };
      default:
        return {
          primary: '#4CAF50',
          secondary: '#2196F3',
          success: '#4CAF50',
          warning: '#FF9800',
          danger: '#F44336',
          fresh: '#4CAF50',
          expiringSoon: '#FF9800',
          expired: '#F44336',
        };
    }
  }

  /**
   * Get font size multiplier for large text mode
   */
  public getFontSizeMultiplier(): number {
    return this.settings.largeTextMode ? 1.3 : 1.0;
  }

  /**
   * Get high contrast styles
   */
  public getHighContrastStyles() {
    if (!this.settings.highContrastMode) {
      return {};
    }

    return {
      backgroundColor: '#000000',
      color: '#FFFFFF',
      borderColor: '#FFFFFF',
      borderWidth: 2,
    };
  }

  /**
   * Provide haptic feedback
   */
  public async provideHapticFeedback(type: 'light' | 'medium' | 'heavy' = 'medium') {
    // Haptic feedback implementation would go here
    // This is a placeholder for the actual implementation
    console.log(`Haptic feedback: ${type}`);
  }

  /**
   * Register common voice commands
   */
  public registerCommonCommands() {
    this.registerVoiceCommand('go back', () => {
      // Navigation back logic
      console.log('Voice command: Go back');
    });

    this.registerVoiceCommand('scan barcode', () => {
      // Navigate to barcode scanner
      console.log('Voice command: Scan barcode');
    });

    this.registerVoiceCommand('add item', () => {
      // Navigate to add item screen
      console.log('Voice command: Add item');
    });

    this.registerVoiceCommand('show settings', () => {
      // Navigate to settings
      console.log('Voice command: Show settings');
    });

    this.registerVoiceCommand('read items', () => {
      // Read out the list of items
      console.log('Voice command: Read items');
    });
  }
}

// Export singleton instance
export default new AccessibilityService();
