export interface ApiResponseGenericPagination<T> {
  currentPage: number;
  pageSize: number;
  totalPages: number;
  totalElements: number;
  sliceTotalElements: number;
  list: T[];
}
