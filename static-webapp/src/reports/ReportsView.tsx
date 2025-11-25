import { Typography, Layout, Button, Space } from "antd";
import React, { useState } from "react";
import { addressProvider } from "../addressProvider";
import { LocalDate } from "../store/viewcontext/TimePeriod";
import { PeriodSelector } from "./PeriodSelector";

const { Title } = Typography;
const { Content } = Layout;

interface ReportsViewProps {
  projectId: string;
  from: LocalDate;
  idToken: string;
}

export const ReportsView: React.FC<ReportsViewProps> = props => {
  const { projectId, from, idToken } = props;
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
      // Send GET request with JWT token in Authorization header
      // The server will verify this token before sending the file
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${idToken}`, // JWT token from Redux store
          'Content-Type': 'application/json'
        }
      });

      // Check if the server returned an error (4xx or 5xx status code)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Convert the response body to a Blob (Binary Large Object)
      // This represents the file data in memory
      const blob = await response.blob();
      
      // Create a temporary URL that points to the blob in memory
      // This URL can be used to trigger a download in the browser
      const downloadUrl = window.URL.createObjectURL(blob);
      
      // Extract the filename from the Content-Disposition header sent by the server
      // Example header: "Content-Disposition: attachment; filename=report.xlsx"
      const contentDisposition = response.headers.get('Content-Disposition');
      
      if (!contentDisposition) {
        throw new Error('Content-Disposition header is missing from server response');
      }
      
      // Regex to parse filename from Content-Disposition header
      // Matches patterns like: filename="report.xlsx" or filename=report.xlsx
      const filenameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      
      if (!filenameMatch || !filenameMatch[1]) {
        throw new Error('Could not extract filename from Content-Disposition header');
      }
      
      // Remove quotes from filename if present
      const filename = filenameMatch[1].replace(/['"]/g, '');
      
      // Programmatically create a temporary <a> element to trigger the download
      const a = document.createElement('a');
      a.href = downloadUrl; // Set the blob URL as the link target
      a.download = filename; // Set the download attribute with the filename
      document.body.appendChild(a); // Add to DOM (required for Firefox)
      a.click(); // Trigger the download
      
      // Clean up: revoke the temporary URL to free memory
      window.URL.revokeObjectURL(downloadUrl);
      // Remove the temporary <a> element from the DOM
      document.body.removeChild(a);
    } catch (error) {
      console.error('Error downloading file:', error);
    }
  }

  const [fromYear, setFromYear] = useState<number | undefined>(from.year);
  const [fromMonth, setFromMonth] = useState<number | undefined>(from.month);

  const [toYear, setToYear] = useState<number | undefined>(from.year);
  const [toMonth, setToMonth] = useState<number | undefined>(from.month);

  return (
    <Layout style={{ padding: 10 }}>
      <Content>
        <Title level={1}>Raporty</Title>

        <Space direction="vertical">
          <PeriodSelector suffix="OD:" year={fromYear} month={fromMonth} onYearChanged={setFromYear} onMonthChanged={setFromMonth} />
          <PeriodSelector suffix="DO:" year={toYear} month={toMonth} onYearChanged={setToYear} onMonthChanged={setToMonth} />

          <Button
            disabled={fromYear == null || fromMonth == null}
            type="link"
            onClick={() => { openInNewTab(addressProvider().host + `/api/raporty/klienci/${projectId}/${fromYear}/${fromMonth}`); }}
          >
            Raport miesięczny - załączniki do faktur
          </Button>

          <Button
            type="link"
            onClick={() => {
            downloadWithToken(addressProvider().host + `/api/raporty/2/${projectId}?yearFrom=${fromYear}&monthFrom=${fromMonth}&yearTo=${toYear}&monthTo=${toMonth}`);
          }}>
            Zestawienie sumaryczne godzin
          </Button>

          <Button
            type="link"
            onClick={() => {
            openInNewTab(addressProvider().host + `/api/raporty/3/${projectId}`);
          }}>
            Lista klientów przypisanych do operatorów
          </Button>
        </Space>
      </Content>
    </Layout>
  );
};

