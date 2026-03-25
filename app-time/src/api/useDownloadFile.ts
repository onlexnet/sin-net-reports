import { gql, useLazyQuery } from '@apollo/client';
import { QueryDownloadFileArgs, FileDownloadResult } from '../components/.generated/components';

// GraphQL query for downloading files
const DOWNLOAD_FILE_QUERY = gql`
  query DownloadFile($projectId: ID!, $year: Int!, $month: Int!) {
    downloadFile(projectId: $projectId, year: $year, month: $month) {
      fileName
      content
      contentType
    }
  }
`;

// Custom hook to use the download file query
export const useDownloadFile = () => {
  const [downloadFile, { loading, error, data }] = useLazyQuery<
    { downloadFile: FileDownloadResult }, 
    QueryDownloadFileArgs
  >(DOWNLOAD_FILE_QUERY);

  return {
    downloadFile,
    loading,
    error,
    data: data?.downloadFile,
  };
};