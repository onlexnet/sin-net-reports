import React, { useState } from "react";
import { Button } from "components/ui/button";
import { addressProvider } from "../addressProvider";
import { LocalDate } from "../store/viewcontext/TimePeriod";
import { PeriodSelector } from "./PeriodSelector";

interface ReportsViewProps {
  projectId: string;
  from: LocalDate;
  idToken: string;
}

export const ReportsView: React.FC<ReportsViewProps> = props => {
  const { projectId, from, idToken } = props;
  const reportLinkClassName = "justify-start px-0 text-foreground hover:text-foreground/90";
  const openInNewTab = (url: string) => {
    window.open(url, '_blank');
  }

  /**
   * Downloads a file from the server with JWT token authentication.
   * Uses fetch API to send the token in the Authorization header.
   * 
   * @param url - The URL of the file to download
   */
  const downloadWithToken = async (url: string) => {
    try {
      console.log('🔵 Starting download from:', url);
      
      // Send GET request with JWT token in Authorization header
      // The server will verify this token before sending the file
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${idToken}`, // JWT token from Redux store
        }
      });

      console.log('🔵 Response status:', response.status);
      console.log('🔵 Response headers:', Object.fromEntries(response.headers.entries()));

      // Check if the server returned an error (4xx or 5xx status code)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Convert the response body to a Blob (Binary Large Object)
      // This represents the file data in memory
      const blob = await response.blob();
      console.log('🔵 Blob created - size:', blob.size, 'bytes, type:', blob.type);
      
      // Create a temporary URL that points to the blob in memory
      // This URL can be used to trigger a download in the browser
      const downloadUrl = window.URL.createObjectURL(blob);
      console.log('🔵 Created blob URL:', downloadUrl);
      
      // Extract the filename from the Content-Disposition header sent by the server
      // Example header: "Content-Disposition: inline; filename=report 2025-11.pdf"
      // NOTE: The header might not be accessible due to CORS - server needs to send:
      //       Access-Control-Expose-Headers: Content-Disposition
      const contentDisposition = response.headers.get('Content-Disposition');
      console.log('🔵 Content-Disposition:', contentDisposition);
      
      let filename = `raport_${new Date().getTime()}.pdf`; // Default fallback
      
      if (contentDisposition) {
        // Regex to parse filename from Content-Disposition header
        // Handles both quoted and unquoted filenames, including those with spaces
        const filenameMatch = contentDisposition.match(/filename\s*=\s*"?([^";\n]+)"?/);
        console.log('🔵 Filename regex match:', filenameMatch);
        
        if (filenameMatch && filenameMatch[1]) {
          // Trim any whitespace from the filename
          filename = filenameMatch[1].trim();
          console.log('🔵 Extracted filename:', filename);
        }
      } else {
        console.warn('⚠️ Content-Disposition header is NOT accessible (CORS issue?)');
        console.warn('⚠️ Server needs: Access-Control-Expose-Headers: Content-Disposition');
        console.log('🔵 Using fallback filename:', filename);
      }
      
      // Programmatically create a temporary <a> element to trigger the download
      const a = document.createElement('a');
      a.href = downloadUrl; // Set the blob URL as the link target
      a.download = filename; // Set the download attribute with the filename
      document.body.appendChild(a); // Add to DOM (required for Firefox)
      console.log('🔵 Created download link element, triggering click...');
      a.click(); // Trigger the download
      console.log('✅ Download triggered successfully!');
      
      // Clean up after a short delay to ensure download starts
      setTimeout(() => {
        window.URL.revokeObjectURL(downloadUrl);
        document.body.removeChild(a);
        console.log('🔵 Cleaned up blob URL and DOM element');
      }, 100);
    } catch (error) {
      console.error('❌ Error downloading file:', error);
      alert(`Failed to download file: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
  }

  const [fromYear, setFromYear] = useState<number | undefined>(from.year);
  const [fromMonth, setFromMonth] = useState<number | undefined>(from.month);

  const [toYear, setToYear] = useState<number | undefined>(from.year);
  const [toMonth, setToMonth] = useState<number | undefined>(from.month);

  return (
    <div className="space-y-4">
        <h1 className="text-3xl font-semibold tracking-tight">Raporty</h1>

        <div className="flex flex-col gap-2">
          <PeriodSelector suffix="OD:" year={fromYear} month={fromMonth} onYearChanged={setFromYear} onMonthChanged={setFromMonth} />
          <PeriodSelector suffix="DO:" year={toYear} month={toMonth} onYearChanged={setToYear} onMonthChanged={setToMonth} />

          <Button
            disabled={fromYear == null || fromMonth == null}
            variant="link"
            className={reportLinkClassName}
            onClick={() => { openInNewTab(addressProvider().host + `/api/raporty/klienci/${projectId}/${fromYear}/${fromMonth}`); }}
          >
            Raport miesięczny - załączniki do faktur
          </Button>

          <Button
            variant="link"
            className={reportLinkClassName}
            onClick={() => {
            downloadWithToken(addressProvider().host + `/api/raporty/2/${projectId}?yearFrom=${fromYear}&monthFrom=${fromMonth}&yearTo=${toYear}&monthTo=${toMonth}`);
          }}>
            Zestawienie sumaryczne godzin
          </Button>

          <Button
            variant="link"
            className={reportLinkClassName}
            onClick={() => {
            openInNewTab(addressProvider().host + `/api/raporty/3/${projectId}`);
          }}>
            Lista klientów przypisanych do operatorów
          </Button>
        </div>
    </div>
  );
};
