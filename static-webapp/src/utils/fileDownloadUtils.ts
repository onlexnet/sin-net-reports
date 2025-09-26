/**
 * Utility functions for handling file downloads from base64 encoded data
 */

export interface FileDownloadData {
  fileName: string;
  content: string; // base64 encoded content
  contentType: string;
}

/**
 * Downloads a file from base64 encoded data to the user's local machine
 * @param fileData - Object containing file name, base64 content, and content type
 */
export const downloadBase64File = (fileData: FileDownloadData): void => {
  try {
    // Convert base64 to blob
    const binaryString = window.atob(fileData.content);
    const bytes = new Uint8Array(binaryString.length);
    
    for (let i = 0; i < binaryString.length; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }

    const blob = new Blob([bytes], { type: fileData.contentType });

    // Create download link
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    
    link.href = url;
    link.download = fileData.fileName;
    
    // Trigger download
    document.body.appendChild(link);
    link.click();
    
    // Cleanup
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
  } catch (error) {
    console.error('Failed to download file:', error);
    throw new Error('Failed to download file. The file data may be corrupted.');
  }
};

/**
 * Validates if the provided content is valid base64
 * @param content - base64 string to validate
 * @returns boolean indicating if the content is valid base64
 */
export const isValidBase64 = (content: string): boolean => {
  try {
    return btoa(atob(content)) === content;
  } catch (err) {
    return false;
  }
};