import axios from 'axios';

const API_URL = process.env.VUE_APP_API_URL || 'http://localhost:8081/api';
const INSURANCE_API_URL = process.env.VUE_APP_INSURANCE_API_URL || 'http://localhost:8081/api2';

class PrescriptionService {
  /**
   * Verifica una receta médica usando el código de aprobación del seguro
   * @param {string} approvalCode - Código de aprobación del seguro
   */
  async verifyPrescription(approvalCode) {
    try {
      const response = await axios.get(`${INSURANCE_API_URL}/service-approvals/check/${approvalCode}`);
      return response.data;
    } catch (error) {
      console.error('Error al verificar la receta:', error);
      throw error;
    }
  }

  /**
   * Procesa una receta para dispensar medicamentos
   * @param {Object} prescriptionData - Datos de la receta a procesar
   */
  async processPrescription(prescriptionData) {
    try {
      const response = await axios.post(`${API_URL}/prescriptions/process`, prescriptionData);
      return response.data;
    } catch (error) {
      console.error('Error al procesar la receta:', error);
      throw error;
    }
  }

  /**
   * Marca una receta como completada en el sistema de seguros
   * @param {string} approvalCode - Código de aprobación del seguro
   */
  async completePrescription(approvalCode) {
    try {
      const response = await axios.put(`${INSURANCE_API_URL}/service-approvals/complete/${approvalCode}`);
      return response.data;
    } catch (error) {
      console.error('Error al completar la receta:', error);
      throw error;
    }
  }

  /**
   * Obtiene el historial de recetas procesadas
   * @param {Object} filters - Filtros para la búsqueda
   */
  async getPrescriptionHistory(filters = {}) {
    try {
      const response = await axios.get(`${API_URL}/prescriptions/history`, { 
        params: filters 
      });
      return response.data;
    } catch (error) {
      console.error('Error al obtener el historial de recetas:', error);
      throw error;
    }
  }

  /**
   * Obtiene el detalle de una receta específica
   * @param {string} prescriptionId - ID de la receta
   */
  async getPrescriptionDetails(prescriptionId) {
    try {
      const response = await axios.get(`${API_URL}/prescriptions/${prescriptionId}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener detalles de la receta:', error);
      throw error;
    }
  }

  /**
   * Verifica si un medicamento está disponible en inventario
   * @param {string} medicationCode - Código del medicamento
   * @param {number} quantity - Cantidad requerida
   */
  async checkMedicationAvailability(medicationCode, quantity) {
    try {
      const response = await axios.get(`${API_URL}/inventory/check`, {
        params: {
          code: medicationCode,
          quantity: quantity
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error al verificar disponibilidad del medicamento:', error);
      throw error;
    }
  }

  /**
   * Actualiza el inventario después de dispensar medicamentos
   * @param {Array} medications - Lista de medicamentos dispensados
   */
  async updateInventoryAfterDispensing(medications) {
    try {
      const response = await axios.post(`${API_URL}/inventory/update-after-dispensing`, {
        medications: medications
      });
      return response.data;
    } catch (error) {
      console.error('Error al actualizar el inventario:', error);
      throw error;
    }
  }

  /**
   * Genera un recibo para la receta procesada
   * @param {Object} receiptData - Datos para el recibo
   */
  async generateReceipt(receiptData) {
    try {
      const response = await axios.post(`${API_URL}/receipts/generate`, receiptData);
      return response.data;
    } catch (error) {
      console.error('Error al generar el recibo:', error);
      throw error;
    }
  }

  /**
   * Verifica si un medicamento está cubierto por el seguro
   * @param {string} medicationCode - Código del medicamento
   * @param {string} approvalCode - Código de aprobación del seguro
   */
  async checkInsuranceCoverage(medicationCode, approvalCode) {
    try {
      const response = await axios.get(`${INSURANCE_API_URL}/medication-coverage/check`, {
        params: {
          medicationCode,
          approvalCode
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error al verificar cobertura del medicamento:', error);
      throw error;
    }
  }
}

export default new PrescriptionService(); 